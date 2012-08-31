package com.androhi;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.androhi.TagScanActivityTest \
 * com.androhi.tests/android.test.InstrumentationTestRunner
 */
public class TagScanActivityTest extends ActivityInstrumentationTestCase2<TagScanActivity> {

    public TagScanActivityTest() {
        super("com.androhi", TagScanActivity.class);
    }

}
