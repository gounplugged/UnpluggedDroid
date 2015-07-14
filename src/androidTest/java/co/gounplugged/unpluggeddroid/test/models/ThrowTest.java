package co.gounplugged.unpluggeddroid.test.models;

import android.test.AndroidTestCase;

import co.gounplugged.unpluggeddroid.models.Throw;

/**
 * Created by Marvin Arnold on 13/07/15.
 */
public class ThrowTest extends AndroidTestCase {
    public void testIsValidThrow() {
        assertTrue(Throw.isValidThrow(Throw.THROW_IDENTIFIER));
        assertTrue(Throw.isValidThrow("qZYZqQQQwZZqfQ-----BEGIN PGP MESSAGE-----hQIMAwNJDWvmOi2RARAAk4ZS6H6XHeqS5Z1CQ9wXZNTi0IKO8tQCTIIcjJdCIZz8MtmrawnlSnQgm2JP7WhiKJdmW04QYdvvb0aTWyXminCu+X+en1haGlw/W+Kn4YCn 240haYSiMlFjm01xkTj+DO/bVDoVVvCNwDpc0ug/OshvgGIbCTDtgrgSXX83p665huLPuxtbIHaRsI84CPqhpIEDtvIoKbvRcRYyuUsljL1GvhnqvTqxoV95qHe95GUtPL1xUD58zsG8m5wb7Wh6Yo3HvvRZJ2x4mkt2ORZ8GmKH1/Vsew+k5IvKetDOmrDoROYJZkMxIKwRnWIX809dGfU3ZFamYqJoLoYxXfoi5EV/t2Y6G0qrA6AK6E16rlIxAGE4NQGmROJ4ibJTni3RKJ4iF6ISiYLsTBuMyiPq3sCuSAoMEXYrW8t68w5zmhMyMqP7OuFZDRP+I9wcnhtL/U0jC015w9zYhxjNLoQEU8fTI5Y7kzzx1VcAkjUTuVZF KqLdSjWB5m9cbmB6Gl15Ev5MOyyL2GBmbHCK9n8U0wUXgvqSqnILp8TvTKCPQekzwr8bPjx1dZfLEf2wIjCl/lXlb5nHcbtOLH4+yDRRmoRx2IW4vwRr7b+EbuyycNcY zphSLdUAPiALCj/KdVKPy4J9QhJwlPPvNrYV7K7olZKxKXsh2b7rRDgrtH9diYnSTQF3OJlHwoLZaqlJFow57UI86LvMFze5T3t5f/losVOfaTgSi//uBmtyRZEFqmMN5mOofdfCWysbBMLi/XL+WtOICjBUlEZ60sROQ7fI =eLpT-----END PGP MESSAGE-----"));
    }
}
