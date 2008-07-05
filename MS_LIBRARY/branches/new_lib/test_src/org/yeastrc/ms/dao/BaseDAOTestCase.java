/**
 * BaseDAOTestCase.java
 * @author Vagisha Sharma
 * Jul 4, 2008
 * @version 1.0
 */
package org.yeastrc.ms.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dto.MsPeptideSearchResult;
import org.yeastrc.ms.dto.MsProteinMatch;
import org.yeastrc.ms.dto.MsSearchDynamicMod;
import org.yeastrc.ms.dto.MsSearchMod;
import org.yeastrc.ms.dto.MsSearchResultDynamicMod;
import org.yeastrc.ms.dto.MsSequenceDatabase;

import junit.framework.TestCase;

/**
 * 
 */
public class BaseDAOTestCase extends TestCase {

    MsPeptideSearchDAO searchDao = DAOFactory.instance().getMsPeptideSearchDAO();
    MsPeptideSearchResultDAO resultDao = DAOFactory.instance().getMsPeptideSearchResultDAO();
    MsSequenceDatabaseDAO seqDbDao = DAOFactory.instance().getMsSequenceDatabaseDAO();
    MsSearchModDAO modDao = DAOFactory.instance().getMsSearchModDAO();
    MsProteinMatchDAO matchDao = DAOFactory.instance().getMsProteinMatchDAO();

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }



    protected MsSequenceDatabase makeSequenceDatabase(String serverAddress, String serverPath,
            Integer seqLength, Integer proteinCount) {
        MsSequenceDatabase db = new MsSequenceDatabase();
        if (serverAddress != null)
            db.setServerAddress(serverAddress);
        if (serverPath != null)
            db.setServerPath(serverPath);
        if (seqLength != null)
            db.setSequenceLength(seqLength);
        if (proteinCount != null)
            db.setProteinCount(proteinCount);
        return db;
    }

    protected MsPeptideSearchResult makeSearchResult(int searchId, int scanId, int charge,
            String peptide, boolean addPrMatch, boolean addDynaMod) {

        MsPeptideSearchResult result = makeSearchResult(searchId, scanId, charge, peptide);

        // add protein matches
        if (addPrMatch)     addProteinMatches(result);

        // add dynamic modifications
        if (addDynaMod)     addResultDynamicModifications(result, searchId);

        return result;
    }

    protected MsPeptideSearchResult makeSearchResult(int searchId, int scanId, int charge, String peptide) {
        MsPeptideSearchResult result = new MsPeptideSearchResult();
        result.setSearchId(searchId);
        result.setScanId(scanId);
        result.setCharge(charge);
        result.setPeptide(peptide);

        return result;
    }

    protected void addProteinMatches(MsPeptideSearchResult result) {
        MsProteinMatch match1 = new MsProteinMatch();
        match1.setAccession("Accession_"+result.getPeptide()+"_1");
        match1.setDescription("Description_"+result.getPeptide()+"_1");

        result.addProteinMatch(match1);

        MsProteinMatch match2 = new MsProteinMatch();
        match2.setAccession("Accession_"+result.getPeptide()+"_2");

        result.addProteinMatch(match2);
    }

    protected void addResultDynamicModifications(MsPeptideSearchResult result, int searchId) {

        List<MsSearchDynamicMod> dynaMods = modDao.loadDynamicModificationsForSearch(searchId);

        List<MsSearchResultDynamicMod> resultDynaMods = new ArrayList<MsSearchResultDynamicMod>(dynaMods.size());
        int pos = 1;
        for (MsSearchDynamicMod mod: dynaMods) {
            MsSearchResultDynamicMod resMod = new MsSearchResultDynamicMod();
            resMod.setModificationId(mod.getId());
            resMod.setModificationMass(mod.getModificationMass());
            resMod.setModificationPosition(pos++);
            resMod.setModificationSymbol(mod.getModificationSymbol());
            resMod.setModifiedResidue(mod.getModifiedResidue());
            resultDynaMods.add(resMod);
        }

        result.setDynamicModifications(resultDynaMods);
    }

    protected MsSearchMod makeStaticMod(Integer searchId, char modChar, String modMass) {
        MsSearchMod mod = new MsSearchMod();
        if (searchId != null)
            mod.setSearchId(searchId);
        mod.setModifiedResidue(modChar);
        mod.setModificationMass(new BigDecimal(modMass));
        return mod;
    }

    protected MsSearchDynamicMod makeDynamicMod(Integer searchId, char modChar, String modMass,
            char modSymbol) {
        MsSearchDynamicMod mod = new MsSearchDynamicMod();
        if (searchId != null)
            mod.setSearchId(searchId);
        mod.setModifiedResidue(modChar);
        mod.setModificationMass(new BigDecimal(modMass));
        mod.setModificationSymbol(modSymbol);
        return mod;
    }
}
