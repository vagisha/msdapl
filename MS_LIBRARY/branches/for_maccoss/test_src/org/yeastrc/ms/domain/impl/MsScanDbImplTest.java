package org.yeastrc.ms.domain.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.run.impl.ScanBean;
import org.yeastrc.ms.util.PeakConverterDouble;
import org.yeastrc.ms.util.PeakStringBuilder;

public class MsScanDbImplTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testParsePeaksAsString() throws IOException, ClassNotFoundException {
        ScanBean scanDb = new ScanBean();
        PeakStringBuilder builder = new PeakStringBuilder();
        List<String[]> peaks = new ArrayList<String[]>(10);
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            String[] peak = new String[2];
            peak[0] = Double.toString(r.nextDouble());
            peak[1] = Double.toString(r.nextDouble());
            peaks.add(peak);
            builder.addPeak(peak[0], peak[1]);
        }
        scanDb.setPeakData(builder.getPeaksAsString());
        List<double[]> peakList = new PeakConverterDouble().convert(scanDb.peakDataString());
        int i = 0;
        for (double[] peak: peakList) {
            String[] peakStr = peaks.get(i);
            assertEquals(peak[0], Double.parseDouble(peakStr[0]));
            assertEquals(peak[1], Double.parseDouble(peakStr[1]));
            i++;
        }
        assertEquals(10, i);
    }

}
