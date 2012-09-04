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
    };
}
