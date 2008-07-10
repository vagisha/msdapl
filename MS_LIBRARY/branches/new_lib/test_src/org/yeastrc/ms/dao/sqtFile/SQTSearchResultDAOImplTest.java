package org.yeastrc.ms.dao.sqtFile;

import java.math.BigDecimal;

import org.yeastrc.ms.domain.db.MsPeptideSearchResult;
import org.yeastrc.ms.domain.sqtFile.ISQTSearchResult;
import org.yeastrc.ms.domain.sqtFile.db.SQTSearchResult;

public class SQTSearchResultDAOImplTest extends SQTBaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsOnSqtSearchResult() {
        
        // try to get the result for a result id that does not exist in the table
        ISQTSearchResult res = sqtResDao.load(1);
        assertNull(res);
        
        // insert one result in to the table
        MsPeptideSearchResult parentResult = makeSearchResult(1, 100, 3, "PEPTIDE");
        SQTSearchResult sqtResult = new SQTSearchResult(parentResult);
        
        // we have not yet set any of the SQT file specific values. Saving the 
        // result at this point should fail
        try {
            sqtResDao.save(sqtResult);
            fail("Was able to save SQTSearchResult with null values!");
        }
        catch (RuntimeException e){
            resultDao.deleteResultsForSearch(1);
        }
        
        sqtResult.setxCorrRank(1);
        sqtResult.setxCorr(new BigDecimal("0.50"));
        sqtResult.setSpRank(1);
        sqtResult.setSp(new BigDecimal("123.4"));
        sqtResult.setDeltaCN(new BigDecimal("0.001"));
        
        //TODO make sure all values from parentResult are copied to sqtResult
        int resultId = sqtResDao.save(sqtResult);
        
        // make sure everything got saved
        assertNotNull(resultDao.load(resultId));
        SQTSearchResult sqtResult_db = sqtResDao.load(resultId);
        assertNotNull(sqtResult_db);
        assertEquals(sqtResult_db.getSearchId(), parentResult.getSearchId());
        assertEquals(sqtResult_db.getScanId(), parentResult.getScanId());
        assertEquals(sqtResult_db.getCharge(), parentResult.getCharge());
        assertEquals(sqtResult_db.getPeptide(), parentResult.getPeptide());
        assertEquals(sqtResult_db.getCalculatedMass(), parentResult.getCalculatedMass());
        assertEquals(sqtResult_db.getNumIonsMatched(), parentResult.getNumIonsMatched());
        assertEquals(sqtResult_db.getNumIonsPredicted(), parentResult.getNumIonsPredicted());
        assertEquals(sqtResult_db.getPreResidue(), parentResult.getPreResidue());
        assertEquals(sqtResult_db.getPostResidue(), parentResult.getPostResidue());
        assertEquals(sqtResult_db.getValidationStatus(), parentResult.getValidationStatus());
        
        assertEquals(sqtResult_db.getResultId(), resultId);
        assertEquals(sqtResult_db.getxCorrRank(), sqtResult.getxCorrRank());
        assertEquals(sqtResult_db.getxCorr().doubleValue(), sqtResult.getxCorr().doubleValue());
        assertEquals(sqtResult_db.getDeltaCN().doubleValue(), sqtResult.getDeltaCN().doubleValue());
        assertEquals(sqtResult_db.getSpRank(), sqtResult.getSpRank());
        assertEquals(sqtResult_db.getSp().doubleValue(), sqtResult.getSp().doubleValue());
        
        
        // delete the result
        sqtResDao.delete(resultId);
        assertNull(resultDao.load(resultId));
        assertEquals(0, resultDao.loadResultIdsForSearch(1).size());
        assertNull(sqtResDao.load(resultId));
    }
}
