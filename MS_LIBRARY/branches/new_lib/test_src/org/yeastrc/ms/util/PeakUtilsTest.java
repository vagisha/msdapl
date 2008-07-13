package org.yeastrc.ms.util;

import java.io.IOException;

import junit.framework.TestCase;

public class PeakUtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEncodingAndDecoding() throws IOException, ClassNotFoundException {
        
        String peaks = "123.01:98.7;234.01:87.6;345.01:76.5";
        byte[] peakBytes = PeakUtils.encodePeakString(peaks);
        assertEquals(peaks, PeakUtils.decodePeakString(peakBytes));
        
        peaks = "";
        peakBytes = PeakUtils.encodePeakString(peaks);
        assertEquals(peaks, PeakUtils.decodePeakString(peakBytes));
        
        peaks = null;
        peakBytes = PeakUtils.encodePeakString(peaks);
        assertEquals(peaks, PeakUtils.decodePeakString(peakBytes));
    }
}
