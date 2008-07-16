package org.yeastrc.ms.dao.sqtFile;

import java.math.BigDecimal;

import org.yeastrc.ms.dao.MsSearchResultDAOImplTest.MsSearchResultPeptideTest;
import org.yeastrc.ms.dao.MsSearchResultDAOImplTest.MsSearchResultTest;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.SQTSearchResultDb;

public class SQTSearchResultDAOImplTest extends SQTBaseDAOTestCase {

//    public static BigDecimal sp;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSqtSearchResult() {
        
        // try to get the result for a result id that does not exist in the table
        SQTSearchResultDb res = sqtResDao.load(1);
        assertNull(res);
        
        // insert one result in to the table
        SQTSearchResultTest result = makeSQTResult(1, 3, "PEPTIDE"); // searchId = 1; charge = 3;
        
        // we have not yet set any of the SQT file specific values. Saving the 
        // result at this point should fail
        try {
            sqtResDao.save(result, 1, 1);
            fail("Was able to save SQTSearchResult with null values!");
        }
        catch (RuntimeException e){
            resultDao.deleteResultsForSearch(1);
        }
        
        result.setXCorrRank(1);
        result.setXCorr(new BigDecimal("0.50"));
        result.setSpRank(1);
        result.setSp(new BigDecimal("123.4"));
        result.setDeltaCN(new BigDecimal("0.001"));
        
        int resultId = sqtResDao.save(result, 1, 1);
        
        // make sure everything got saved
        assertNotNull(resultDao.load(resultId));
        SQTSearchResultDb sqtResult_db = sqtResDao.load(resultId);
        assertEquals(sqtResult_db.getSearchId(), 1);
        assertEquals(resultId, sqtResult_db.getId());
        checkSearchResult(result, sqtResult_db);
        
        
        // delete the result
        sqtResDao.delete(resultId);
        assertNull(resultDao.load(resultId));
        assertEquals(0, resultDao.loadResultIdsForSearch(1).size());
        assertNull(sqtResDao.load(resultId));
    }
    
    private void checkSearchResult(SQTSearchResult input, SQTSearchResultDb output) {
        super.checkSearchResult(input, output);
        assertEquals(input.getDeltaCN().doubleValue(), output.getDeltaCN().doubleValue());
        assertEquals(input.getSp().doubleValue(), output.getSp().doubleValue());
        assertEquals(input.getxCorr().doubleValue(), output.getxCorr().doubleValue());
        assertEquals(input.getSpRank(), output.getSpRank());
        assertEquals(input.getxCorrRank(), output.getxCorrRank());
    }
    
    private SQTSearchResultTest makeSQTResult(int searchId, int charge,String peptide) {
        SQTSearchResultTest result = new SQTSearchResultTest();
        result.setCharge(charge);
        MsSearchResultPeptideTest resultPeptide = new MsSearchResultPeptideTest();
        resultPeptide.setPeptideSequence(peptide);
        result.setResultPeptide(resultPeptide);
        
//        // add protein matches
//        if (addPrMatch)     addProteinMatches(result);
//
//        // add dynamic modifications
//        if (addDynaMod)     addResultDynamicModifications(resultPeptide, searchId);

        return result;
    }
    public static final class SQTSearchResultTest extends MsSearchResultTest implements SQTSearchResult {

        private BigDecimal deltaCN;
        private int spRank;
        private BigDecimal xCorr;
        private int xCorrRank;
        private BigDecimal sp;

        public BigDecimal getDeltaCN() {
            return deltaCN;
        }

        public void setDeltaCN(BigDecimal deltaCN) {
            this.deltaCN = deltaCN;
        }

        public void setSpRank(int spRank) {
            this.spRank = spRank;
        }

        public void setXCorr(BigDecimal corr) {
            xCorr = corr;
        }

        public void setXCorrRank(int corrRank) {
            xCorrRank = corrRank;
        }

        public void setSp(BigDecimal sp) {
            this.sp = sp;
        }

        public BigDecimal getSp() {
            return sp;
        }

        public int getSpRank() {
            return spRank;
        }

        public BigDecimal getxCorr() {
            return xCorr;
        }

        public int getxCorrRank() {
            return xCorrRank;
        }
    }
}
