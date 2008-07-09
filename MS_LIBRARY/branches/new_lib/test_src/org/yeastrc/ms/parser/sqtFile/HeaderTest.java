package org.yeastrc.ms.parser.sqtFile;

import java.sql.Date;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class HeaderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMultipleDatabases() {
        Header header = new Header();
        String filePath = "/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta";
        assertFalse(header.multipleDatabases(filePath));

        filePath = "     /scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta       ";
        assertFalse(header.multipleDatabases(filePath));

        filePath = "/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta,/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta";
        assertTrue(header.multipleDatabases(filePath));

        filePath = "/scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta, ";
        assertTrue(header.multipleDatabases(filePath));

        filePath = ", /scratch/yates/NCBI-RefSeq_human_na_01-19-2007_reversed.fasta";
        assertTrue(header.multipleDatabases(filePath));

        filePath = "/path/1  /path/2 ";
        assertTrue(header.multipleDatabases(filePath));
    }

    public void testGetTime() {

        Header header = new Header();
        // Example of a valid time string: 01/29/2008, 03:34 AM
        try {
            header.getTime(" 01/29/2008, 03:34 AM ");
        }
        catch (ParseException e) {
            e.printStackTrace();
            fail("Valid time string");
        }
        
        try {
            header.getTime(" 01/29/2008");
            fail("Invalid time string");
        }
        catch (ParseException e) {}
    }
    
    
    public void testGetStartDate() {
        Header header = new Header();
        header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        Date date = header.getSearchDate();
        Calendar myCal = GregorianCalendar.getInstance();
        myCal.setTime(date);
        assertEquals(0, myCal.get(Calendar.MONTH));
        assertEquals(29, myCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(2008, myCal.get(Calendar.YEAR));
        assertEquals(3, myCal.get(Calendar.HOUR));
        assertEquals(34, myCal.get(Calendar.MINUTE));
        assertEquals(0, myCal.get(Calendar.SECOND));
        assertEquals(0, myCal.get(Calendar.MILLISECOND));
        
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 29);
        cal.set(Calendar.MONTH, 0); // months go from 0 to 11
        cal.set(Calendar.YEAR, 2008);
        cal.set(Calendar.HOUR, 3);
        cal.set(Calendar.MINUTE, 34);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        
        assertEquals(date.getTime(), cal.getTimeInMillis());
    }
    
    public void testGetStartDateInvalidDate() {
        Header header = new Header();
        header.addHeaderItem("StartTime", "01/29/2008");
        try {
            header.getSearchDate();
            fail("Invalid start date");
        }
        catch(RuntimeException e){}
    }
    
    public void testGetSearchDurationNoEndTime() {
        Header header = new Header();
        header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        
        assertEquals(0, header.getSearchDuration());
    }
    
    public void testGetSearchDurationWithEndTime() {
        Header header = new Header();
        header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        header.addHeaderItem("EndTime", "01/29/2008, 03:44 AM");
        assertEquals(10, header.getSearchDuration());
    }
    
    public void testGetSearchDurationInvalidEndDate() {
        Header header = new Header();
        header.addHeaderItem("StartTime", "01/29/2008, 03:34 AM");
        header.addHeaderItem("EndTime", "01/29/2008, 03:44");
        try {
            header.getSearchDuration();
            fail("Invalid end date");
        }
        catch(RuntimeException e){}
        
    }

}
