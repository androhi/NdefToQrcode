package com.androhi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: androhi
 * Date: 12/08/23
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class QrcodeViewer extends Activity implements View.OnClickListener {
    private static final String TAG = QrcodeViewer.class.getSimpleName();

    private ImageView mImageView;
    private TextView mTextView;
    private Button mSaveButton;
    private Button mCloseButton;
    private String mUrl;
    private Bitmap mBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_viewer);

        mSaveButton = (Button)findViewById(R.id.save);
        mCloseButton = (Button)findViewById(R.id.close);
        mSaveButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);

        mSaveButton.setEnabled(false);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("URL");
        mImageView = (ImageView)findViewById(R.id.qrcode);
        mTextView = (TextView)findViewById(R.id.text);

        if (mUrl != null) {
            WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int displayWidth = display.getWidth();
            int displayHeight = display.getHeight();
            int qrcodeSize = displayWidth;

            if (displayHeight < displayWidth) {
                qrcodeSize = displayHeight;
            }

            // QRコード生成
            SaveQrcodeTask task = new SaveQrcodeTask(this, qrcodeSize);
            task.execute(mUrl);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveQRcode();
                finish();
                break;
            case R.id.close:
                finish();
                break;
            default:
                break;
        }
    }

    private void saveQRcode() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable)mImageView.getDrawable();
        Bitmap bmp = bitmapDrawable.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        String fileName = getPictureDirectory() + "/qrcode.jpg";
        Log.w(TAG, "fileName:" + fileName);
        try {
            FileOutputStream out = new FileOutputStream(fileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getPictureDirectory() {
        String storagePath = Environment.getExternalStorageDirectory().getPath() + "/" + getString(R.string.app_name);
        File appDir = new File(storagePath);
        Log.d(TAG, "appDir:" + appDir.getPath() + "(" + appDir.exists() + ")");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        return storagePath;
    }

}
