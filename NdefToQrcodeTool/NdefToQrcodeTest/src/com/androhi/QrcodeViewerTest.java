package com.androhi;

import android.test.ActivityInstrumentationTestCase2;

import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created with IntelliJ IDEA.
 * User: androhi
 * Date: 12/09/07
 * Time: 19:27
 */
public class QrcodeViewerTest extends ActivityInstrumentationTestCase2<QrcodeViewer> {

    private QrcodeViewer mQrcodeViewer;

    public QrcodeViewerTest() {
        super(QrcodeViewer.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        mQrcodeViewer = getActivity();
    }

    @SmallTest
    public void testPreConditions() {
    }

    @MediumTest
    public void testUI() {
    }
}
