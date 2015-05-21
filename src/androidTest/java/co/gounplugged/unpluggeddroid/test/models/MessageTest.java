package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.models.Message;

/**
 * Created by Marvin Arnold on 21/05/15.
 */
public class MessageTest extends AndroidTestCase {
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


    public void testIsCompatible() {
        assertTrue(Message.isCompatible("anything "));
        assertFalse(Message.isCompatible("anything"));
    }
}
