package com.androhi;

/**
 * Created with IntelliJ IDEA.
 * User: Shimokawa
 * Date: 12/09/04
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class Definition {
    public static final String[][] TECH_LIST = {
            // MifareClassic
            {
                    android.nfc.tech.MifareClassic.class.getName(),
                    android.nfc.tech.NfcA.class.getName(),
                    android.nfc.tech.Ndef.class.getName()
            },
            // NTAG203
            {
                    android.nfc.tech.MifareUltralight.class.getName(),
                    android.nfc.tech.NfcA.class.getName(),
                    android.nfc.tech.Ndef.class.getName()
            },
            // NTAG203
            {
                    android.nfc.tech.NfcA.class.getName(),
                    android.nfc.tech.Ndef.class.getName()
            },
            {
                    android.nfc.tech.NfcB.class.getName(),
                    android.nfc.tech.Ndef.class.getName()
            },
    };

    // nend用定数
    public static final int NEND_SPOT_ID = 18914;
    public static final String NEND_API_KEY = "1f9fc0db64e3b7a9a579531e56ae642348756fc0";

}
