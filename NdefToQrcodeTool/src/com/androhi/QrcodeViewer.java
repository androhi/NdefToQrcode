package com.androhi;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;
import android.text.format.Time;
import net.nend.android.NendAdView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: androhi
 * Date: 12/08/23
 * Time: 19:14
 */
public class QrcodeViewer extends Activity {
    private static final String TAG = QrcodeViewer.class.getSimpleName();

    private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    private String mUrl;
    private String mFilename;
    private Uri mContentUri;
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_viewer);

        NendAdView nendAdView = new NendAdView(getApplicationContext(),
                                        Definition.NEND_SPOT_ID, Definition.NEND_API_KEY);
        addContentView(nendAdView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        nendAdView.setGravity(Gravity.BOTTOM | Gravity.CENTER);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("URL");
        mFilename = null;

        if (mUrl != null) {
            WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            Point displaySize = new Point();
            // API level 13からはgetWidth/getHeightはdeprecated
            display.getSize(displaySize);

            int qrcodeSize = displaySize.x;
            if (displaySize.y < displaySize.x) {
                qrcodeSize = displaySize.y;
            }

            // QRコード生成
            SaveQrcodeTask task = new SaveQrcodeTask(this, qrcodeSize);
            task.execute(mUrl);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewer, menu);

        mShareActionProvider = (ShareActionProvider)menu.findItem(R.id.menu_share).getActionProvider();
        //mShareActionProvider.setShareIntent(createShareIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                addQRcodeImageToGallery();
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    public void saveQRcode(Bitmap bitmap) {
        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        if (mFilename == null) {
            mFilename = getPictureDirectory() + "/IMG_" + createTakenDate() + ".jpg";
        }
        try {
            FileOutputStream out = new FileOutputStream(mFilename, true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            mContentUri = addQRcodeImageToGallery();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doShare() {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = null;
        if (mFilename != null) {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, mContentUri);
        }
        return shareIntent;
    }

    private String getPictureDirectory() {
        // SDカードの画像フォルダを指定
        String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/" + getString(R.string.app_name);
        File appDir = new File(storagePath);
        if (!appDir.exists()) {
            // アプリ用ディレクトリ作成
            appDir.mkdir();
        }
        return storagePath;
    }

    private String createTakenDate() {
        TimeZone timeZone = TimeZone.getDefault();
        Time time = new Time(timeZone.getID());
        time.setToNow();
        return time.format2445();
    }

    private Uri addQRcodeImageToGallery() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, mUrl);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.DATA, mFilename);

        ContentResolver contentResolver = getContentResolver();
        return contentResolver.insert(IMAGE_URI, contentValues);
    }
}
