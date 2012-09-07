package com.androhi;

import android.test.suitebuilder.TestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created with IntelliJ IDEA.
 * User: androhi
 * Date: 12/09/07
 * Time: 14:39
 *
 * このクラスをテストとして実行すると、全てのテストが実行される。
 */
public class AllTest extends TestSuite {

    public static Test suite() {
        // 配下のJUnitを全て行う
        return new TestSuiteBuilder(AllTest.class)
                .includeAllPackagesUnderHere()
                .build();
    }
}
