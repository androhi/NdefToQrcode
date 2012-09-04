package com.androhi;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

public class TagScanActivity extends Activity {
    @SuppressWarnings("unused")
    private static final String TAG = TagScanActivity.class.getSimpleName();

    /**
     * NFC Forum "URI Record Type Definition"
     *
     * This is a mapping of "URI Identifier Codes" to URI string prefixes,
     * per section 3.2.2 of the NFC Forum URI Record Type Definition document.
     */
    private HashMap<Byte, String> prefixMap = new HashMap<Byte, String>();
    private NfcAdapter mNfcAdapter;

    {
        prefixMap.put((byte) 0x00, "");
        prefixMap.put((byte) 0x01, "http://www.");
        prefixMap.put((byte) 0x02, "https://www.");
        prefixMap.put((byte) 0x03, "http://");
        prefixMap.put((byte) 0x04, "https://");
        prefixMap.put((byte) 0x05, "tel:");
        prefixMap.put((byte) 0x06, "mailto:");
        //prefixMap.put((byte) 0x07, "ftp://anonymous:anonymous@");
        //prefixMap.put((byte) 0x08, "ftp://ftp.");
        //prefixMap.put((byte) 0x09, "ftps://");
        //prefixMap.put((byte) 0x0A, "sftp://");
        //prefixMap.put((byte) 0x0B, "smb://");
        //prefixMap.put((byte) 0x0C, "nfs://");
        //prefixMap.put((byte) 0x0D, "ftp://");
        //prefixMap.put((byte) 0x0E, "dav://");
        //prefixMap.put((byte) 0x0F, "news:");
        //prefixMap.put((byte) 0x10, "telnet://");
        //prefixMap.put((byte) 0x11, "imap:");
        //prefixMap.put((byte) 0x12, "rtsp://");
        //prefixMap.put((byte) 0x13, "urn:");
        //prefixMap.put((byte) 0x14, "pop:");
        //prefixMap.put((byte) 0x15, "sip:");
        //prefixMap.put((byte) 0x16, "sips:");
        //prefixMap.put((byte) 0x17, "tftp:");
        //prefixMap.put((byte) 0x18, "btspp://");
        //prefixMap.put((byte) 0x19, "btl2cap://_");
        //prefixMap.put((byte) 0x1A, "btgoep://");
        //prefixMap.put((byte) 0x1B, "tcpobex://");
        //prefixMap.put((byte) 0x1C, "irdaobex://");
        //prefixMap.put((byte) 0x1D, "file://");
        //prefixMap.put((byte) 0x1E, "urn:epc:id:");
        //prefixMap.put((byte) 0x1F, "urn:epc:tag:");
        //prefixMap.put((byte) 0x20, "urn:epc:pat:");
        //prefixMap.put((byte) 0x21, "urn:epc:raw:");
        //prefixMap.put((byte) 0x22, "urn:epc:");
        //prefixMap.put((byte) 0x23, "urn:nfc:");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onResume() {
        super.onResume();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // NFC非搭載端末
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            // NFC機能OFF
            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            startActivity(intent);
            return;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        IntentFilter[] intentFilters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
        };
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, Definition.TECH_LIST);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "start onNewIntent()");
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            // action属性がない
            return;
        }
        if (!action.equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            // action属性がtech-discoveredでない
            return;
        }

        // NDEFメッセージを取り出す
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage message;

        if (rawMsgs != null) {
            message = (NdefMessage)rawMsgs[0];
        } else {
            // Unknown tag type
            byte[] empty = new byte[] {};
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
            message = new NdefMessage(new NdefRecord[] {record});
        }
        Log.d(TAG, "NdefMessage : " + message);

        try {
            String url = getUrl(message);
            Log.d(TAG, "getUrl() : " + url);
            Intent viewerIntent = new Intent(TagScanActivity.this, QrcodeViewer.class);
            viewerIntent.putExtra("URL", url);
            startActivity(viewerIntent);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private String getUrl(NdefMessage msg) {
        // NDEFレコードからTNFを取り出す
        NdefRecord[] records = msg.getRecords();
        short tnf = records[0].getTnf();

        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            return parseWellKnown(records[0]);
        } else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return parseAbsolute(records[0]);
        }
        throw new IllegalArgumentException("Unknown TNF " + tnf);
    }

    private String parseAbsolute(NdefRecord record) {
        byte[] payload = record.getPayload();
        return new String(payload, Charset.forName("UTF-8"));
    }

    private String parseWellKnown(NdefRecord record) {
        // RTD Uriかチェック
        if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
            byte[] payload = record.getPayload();
            String prefix = prefixMap.get(payload[0]);
            byte[] uriData = Arrays.copyOfRange(payload, 1, payload.length);
            String uri = new String(uriData, Charset.forName("UTF-8"));
            return prefix.concat(uri);
        }
        throw new IllegalArgumentException("Not Support RTD");
    }
}
