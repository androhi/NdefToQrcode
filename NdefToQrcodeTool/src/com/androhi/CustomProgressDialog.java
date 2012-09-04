package com.androhi;

import android.app.Dialog;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: Shimokawa
 * Date: 12/09/04
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.progress_dialog);
    }
}
