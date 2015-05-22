package co.gounplugged.unpluggeddroid.test.utils;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.utils.MessageUtil;

/**
 * Created by Marvin Arnold on 21/05/15.
 */
public class MessageUtilTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
        // TODO Auto-generated method stub
        super.tearDown();
    }


    public void testIsSLCompatible() {
        assertTrue(MessageUtil.isSLCompatible("anything "));
        assertFalse(MessageUtil.isSLCompatible("anything"));
    }
}
