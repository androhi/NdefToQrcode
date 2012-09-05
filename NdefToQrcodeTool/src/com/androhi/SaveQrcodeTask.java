package com.androhi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Shimokawa
 * Date: 12/09/04
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
public class SaveQrcodeTask extends AsyncTask<String, Void, String> {
    private static final String TAG = SaveQrcodeTask.class.getSimpleName();

    private Activity mActivity;
    private CustomProgressDialog mProgressDialog;
    private int mQrcodeSize;
    private Bitmap mBitmap;
    private ImageView mImageView;
    private TextView mTextView;
    private Button mSaveButton;

    public SaveQrcodeTask(Activity activity, int size) {
        mActivity = activity;
        mQrcodeSize = size;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        // プログレスバー設定
        mProgressDialog = new CustomProgressDialog(mActivity);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        mImageView = (ImageView)mActivity.findViewById(R.id.qrcode);
        mTextView = (TextView)mActivity.findViewById(R.id.text);
        mSaveButton = (Button)mActivity.findViewById(R.id.save);
    }

    protected String doInBackground(String... urls) {
        String url = urls[0];
        try {
            mBitmap = createQRCodeByZxing(url, mQrcodeSize);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return url;
    }

    protected void onPostExecute(String url) {
        if(mBitmap != null) {
            mImageView.setImageBitmap(mBitmap);
            mTextView.setText(url);
            mSaveButton.setEnabled(true);
        }
        mProgressDialog.dismiss();
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
}