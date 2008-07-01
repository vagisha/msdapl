package org.yeastrc.ms.dao;

import org.yeastrc.ms.dto.MsRun;

import junit.framework.TestCase;

public class MsRunDAOImplTest extends TestCase {

    private MsRunDAO runDao;
    
    protected void setUp() throws Exception {
        super.setUp();
        runDao = DAOFactory.instance().getMsRunDAO();
    }
    
    public void testSave() {
        MsRun run = new MsRun();
        run.setMsExperimentId(1);
        run.setFileFormat(MsRun.RunFileFormat.MS2.name());
        run.setFileName("my_file1.ms2");
        run.setAcquisitionMethod("Data dependent");
        run.setComment("Dummy run");
        run.set
    }

}
