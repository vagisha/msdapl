package org.yeastrc.ms.util;

import junit.framework.TestCase;

public class PeakStringBuilderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetPeaksAsString() {
        PeakStringBuilder builder = new PeakStringBuilder();
        assertEquals("", builder.getPeaksAsString());
        
        try {
            builder.addPeak("dummy", "100.0");
            fail("Invalid m/z value");
        }
        catch(IllegalArgumentException e) {}
        
        try {
            builder.addPeak("100.0", "dummy");
            fail("Invalid rt value");
        }
        catch(IllegalArgumentException e) {}
        
        builder.addPeak("100.000", "200.1230");
        assertEquals("100:200.123", builder.getPeaksAsString());
        builder.addPeak("123.4", "987.600001000");
        assertEquals("100:200.123;123.4:987.600001", builder.getPeaksAsString());
    }
    
    public void testTrimTrailingZeros() {
        String number = "";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = ".1";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.";
        assertEquals(number, PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.0";
        assertEquals("1", PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.0101000";
        assertEquals("1.0101", PeakStringBuilder.trimTrailingZeros(number));
        
        number = "1.000000";
        assertEquals("1", PeakStringBuilder.trimTrailingZeros(number));
    }

}
