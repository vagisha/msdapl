package org.yeastrc.ms.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

public class Sha1SumCalculatorTest extends TestCase {

    public void testSha1SumForInputStream() {
        String file = "resources/PARC_p75_01_itms.ms2";
        FileInputStream inStr = null;
        try {
            inStr = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("Could not open file");
        }
        try {
            String sha1Sum = Sha1SumCalculator.instance().sha1SumFor(inStr);
            assertEquals("3fc8b86cacacf5eba839ffdccdf87532c377fd6", sha1Sum);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Error calculating SHA-1 sum");
        }
    }

}
