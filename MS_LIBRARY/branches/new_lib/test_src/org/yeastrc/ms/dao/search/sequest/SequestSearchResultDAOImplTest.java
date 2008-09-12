package org.yeastrc.ms.dao.search.sequest;

import java.math.BigDecimal;

import org.yeastrc.ms.dao.search.MsSearchResultDAOImplTest.MsSearchResultTest;
import org.yeastrc.ms.dao.search.sqtfile.SQTBaseDAOTestCase;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;

public class SequestSearchResultDAOImplTest extends SQTBaseDAOTestCase {


    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSqtSearchResult() {
        
        // try to get the result for a result id that does not exist in the table
        SequestSearchResult res = sequestResDao.load(1);
        assertNull(res);
        
        // insert one result in to the table
        SequestSearchResultTest result = makeSequestResult(3, "PEPTIDE"); // charge = 3;
        
        // we have not yet set any of the SQT file specific values. Saving the 
        // result at this point should fail
        try {
            sequestResDao.save(97, result, 45, 32); // searchId = 97; runSearchId = 45; scanId = 32
            fail("Was able to save SQTSearchResult with null values!");
        }
        catch (RuntimeException e){
//            resultDao.deleteResultsForSearch(1);
        }
        
        result.setXCorrRank(1);
        result.setXCorr(new BigDecimal("0.50"));
        result.setSpRank(1);
        result.setSp(new BigDecimal("123.5"));
        result.setDeltaCN(new BigDecimal("0.001"));
        result.setMatchingIons(200);
        
        int resultId = sequestResDao.save(97, result, 45, 32); // searchId = 97; searchResultId = 45; scanId = 32
        
        // make sure everything got saved
        assertNotNull(resultDao.load(resultId));
        SequestSearchResult sqtResult_db = sequestResDao.load(resultId);
        assertEquals(sqtResult_db.getRunSearchId(), 45);
        assertEquals(sqtResult_db.getScanId(), 32);
        assertEquals(resultId, sqtResult_db.getId());
        checkSearchResult(result, sqtResult_db);
        
        // delete the result
        sequestResDao.delete(resultId);
        assertNull(resultDao.load(resultId));
        assertEquals(0, resultDao.loadResultIdsForRunSearch(1).size());
        assertNull(sequestResDao.load(resultId));
    }
    
    private void checkSearchResult(SequestSearchResultIn input, SequestSearchResult output) {
        super.checkSearchResult(input, output);
        
        SequestResultData iData = input.getSequestResultData();
        SequestResultData oData = output.getSequestResultData();
        assertEquals(iData.getDeltaCN().doubleValue(), oData.getDeltaCN().doubleValue());
        assertEquals(iData.getSp().doubleValue(), oData.getSp().doubleValue());
        assertEquals(iData.getxCorr().doubleValue(), oData.getxCorr().doubleValue());
        assertEquals(iData.getSpRank(), oData.getSpRank());
        assertEquals(iData.getxCorrRank(), oData.getxCorrRank());
        assertEquals(iData.getEvalue(), oData.getEvalue());
        assertNull(iData.getCalculatedMass());
        assertNull(oData.getCalculatedMass());
        assertEquals(iData.getMatchingIons(), oData.getMatchingIons());
        assertEquals(iData.getPredictedIons(), oData.getPredictedIons());
    }
    
    private SequestSearchResultTest makeSequestResult(int charge,String peptide) {
        SequestSearchResultTest result = new SequestSearchResultTest();
        result.setCharge(charge);
        SearchResultPeptideBean resultPeptide = new SearchResultPeptideBean();
        resultPeptide.setPeptideSequence(peptide);
        result.setResultPeptide(resultPeptide);

        return result;
    }
    public static final class SequestSearchResultTest extends MsSearchResultTest implements SequestSearchResultIn {

        private BigDecimal deltaCN;
        private int spRank;
        private BigDecimal xCorr;
        private int xCorrRank;
        private BigDecimal sp;
        private BigDecimal calculatedMass;
        private Double evalue;
        private int matchingIons;
        private int predictedIons;

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
        public void setEvalue(double evalue) {
            this.evalue = evalue;
        }
        public void setCalculatedMass(BigDecimal mass) {
            this.calculatedMass = mass;
        }
        public void setMatchingIons(int matchingIons) {
            this.matchingIons = matchingIons;
        }
        public void setPredictedIons(int predictedIons) {
            this.predictedIons = predictedIons;
        }
        @Override
        public SequestResultData getSequestResultData() {
            return new SequestResultData() {
                public BigDecimal getCalculatedMass() {
                    return calculatedMass;
                }
                public BigDecimal getDeltaCN() {
                    return deltaCN;
                }
                public Double getEvalue() {
                    return evalue;
                }
                public int getMatchingIons() {
                    return matchingIons;
                }
                public int getPredictedIons() {
                    return predictedIons;
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
                }};
        }
    }
}
