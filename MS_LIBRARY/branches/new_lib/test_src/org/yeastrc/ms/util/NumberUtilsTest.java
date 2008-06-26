package org.yeastrc.ms.util;

import junit.framework.TestCase;

public class NumberUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTrimTrailingZeros() {
        String number = "";
        assertEquals(number, NumberUtils.trimTrailingZeros(number));
        
        number = "1";
        assertEquals(number, NumberUtils.trimTrailingZeros(number));
        
        number = ".1";
        assertEquals(number, NumberUtils.trimTrailingZeros(number));
        
        number = "1.";
        assertEquals(number, NumberUtils.trimTrailingZeros(number));
        
        number = "1.0";
        assertEquals("1", NumberUtils.trimTrailingZeros(number));
        
        number = "1.0101000";
        assertEquals("1.0101", NumberUtils.trimTrailingZeros(number));
        
        number = "1.000000";
        assertEquals("1", NumberUtils.trimTrailingZeros(number));
    }

}
