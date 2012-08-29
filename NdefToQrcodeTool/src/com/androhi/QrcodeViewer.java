package com.androhi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: androhi
 * Date: 12/08/23
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class QrcodeViewer extends Activity implements View.OnClickListener {
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

            try {
                //TODO:ここはAsyncTaskに分けてProgressBar出そう
                mBitmap = createQRCodeByZxing(mUrl, qrcodeSize);
                if(mBitmap != null) {
                    mImageView.setImageBitmap(mBitmap);
                    mTextView.setText(mUrl);
                    mSaveButton.setEnabled(true);
                }
            } catch(WriterException e) {
                e.printStackTrace();
            }

        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveQRcode(mBitmap);
                break;
            case R.id.close:
                finish();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    public Bitmap createQRCodeByZxing(String contents, int size) throws WriterException {
        // QRコードをエンコードするクラス
        QRCodeWriter qrWriter = new QRCodeWriter();

        // 異なる型の値を入れるためgenericは使えない
        @SuppressWarnings("rawtypes")
        Hashtable encodeHint = new Hashtable();

        // 日本語を扱うためにShift-JISを指定
        encodeHint.put(EncodeHintType.CHARACTER_SET, "shiftjis");

        /*
           * エラー修復レベルを指定
           * L  7%が復元可能
           * M 15%が復元可能
           * Q 25%が復元可能
           * H 30%が復元可能
           */
        encodeHint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix qrCodeData = qrWriter.encode(contents, BarcodeFormat.QR_CODE, size, size, encodeHint);

        // QRコードのbitmap画像を生成
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.argb(255, 255, 255, 255));
        for(int x = 0; x < qrCodeData.getWidth(); x++) {
            for(int y = 0; y < qrCodeData.getHeight(); y++) {
                if(qrCodeData.get(x, y)) {
                    // trueはBlack
                    bitmap.setPixel(x, y, Color.BLACK);
                } else {
                    // falseはWhite
                    bitmap.setPixel(x, y, Color.WHITE);
                }
            }
        }

        return bitmap;
    }

    private void saveQRcode(Bitmap bitmap) {
        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream("qrcode.jpg"));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
