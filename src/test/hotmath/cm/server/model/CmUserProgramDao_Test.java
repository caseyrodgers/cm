package hotmath.cm.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.testset.ha.StudentUserProgramModel;

public class CmUserProgramDao_Test extends CmDbTestCase { 
    
    public CmUserProgramDao_Test(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_test == null)
            setupDemoAccountTest();
    }
    
    
    public void testLoadUserProgram() throws Exception {
        StudentUserProgramModel  supm = CmUserProgramDao.getInstance().loadProgramInfo(conn, _test.getProgramInfo().getId());
        assertTrue(supm.getId().equals(_test.getProgramInfo().getId()));
    }
    
    public void testSetAsComplete() throws Exception {
        boolean res = CmUserProgramDao.getInstance().setProgramAsComplete(conn, _test.getProgramInfo().getId(), true);
        assertTrue(res);
        
        res = CmUserProgramDao.getInstance().setProgramAsComplete(conn, _test.getProgramInfo().getId(), false);
        assertTrue(res);
    }
}
