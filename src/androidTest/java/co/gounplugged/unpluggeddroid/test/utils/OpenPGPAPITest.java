package co.gounplugged.unpluggeddroid.test.utils;

import android.test.ServiceTestCase;

/**
 * Created by Marvin Arnold on 10/06/15.
 */
public class OpenPGPAPITest extends ServiceTestCase {
    /**
     * Constructor
     *
     * @param serviceClass The type of the service under test.
     */
    public OpenPGPAPITest(Class serviceClass) {
        super(serviceClass);
    }
}

//although ServiceTestCase does seem to go in the right direction,
//ServiceTestCase.bindService seems to be what you need