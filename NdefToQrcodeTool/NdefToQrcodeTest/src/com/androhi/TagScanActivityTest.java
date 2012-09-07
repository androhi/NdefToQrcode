package com.androhi;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

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
    private TagScanActivity mActivity;
    private TextView mTextView;
    private String resourceString;

    public TagScanActivityTest() {
        super(TagScanActivity.class);
    }

    /**
     * setUp() メソッド
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // タッチOFF
        setActivityInitialTouchMode(false);
        mActivity = getActivity();
        mTextView = (TextView)mActivity.findViewById(R.id.scan_text);
        resourceString = mActivity.getString(R.string.tag_scan_text);
    }

    /**
     * 初期条件テスト
     */
    @SmallTest
    public void testPreConditions() {
        assertNotNull(mTextView);
    }

    /**
     * UIテスト
     */
    @MediumTest
    public void testUI() {
        assertEquals(resourceString, (String)mTextView.getText());
    }
}
