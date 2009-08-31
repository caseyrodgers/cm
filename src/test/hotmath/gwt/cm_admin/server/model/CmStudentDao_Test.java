package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.testset.ha.StudentUserProgramModel;

public class CmStudentDao_Test extends CmDbTestCase {

    /**
     * @TODO: create method to generate test accounts for testing
     * 
     */
    static int TEST_ID;

    public CmStudentDao_Test(String name) throws Exception {
        super(name);
        
        if(TEST_ID == 0)
            TEST_ID = setupDemoAccount();
    }

    CmStudentDao _dao;

    protected void setUp() throws Exception {
        super.setUp();
        _dao = new CmStudentDao();
    }

    
    
    public void moveToNextQuizSegmentSlot() throws Exception {
        _dao.moveToNextQuizSegmentSlot(conn, TEST_ID);
        
        StudentActiveInfo active1 = _dao.moveToNextQuizSegmentSlot(conn, TEST_ID);
        StudentActiveInfo active = _dao.moveToNextQuizSegmentSlot(conn, TEST_ID);
        assertNotNull(active);
        
        assertTrue(active1.getActiveSegmentSlot() != active.getActiveSegmentSlot());
    }
        
    
    public void testgetTotalInmHViewCount() throws Exception {
        Integer count = _dao.getTotalInmHViewCount(conn,TEST_ID);
        assertNotNull(count);
    }
    
    public void testGetStudentBasic() throws Exception {
        StudentModelI sm = _dao.getStudentModelBasic(conn,TEST_ID);
        assertNotNull(sm);
        assertTrue(sm.getUid() > 0);
    }
    
    public void testGetStudentActive() throws Exception {
        StudentActiveInfo active = _dao.loadActiveInfo(conn, TEST_ID);
        assertNotNull(active);
    }
    
    public void testGetStudentProgram() throws Exception {
        StudentUserProgramModel pi = _dao.loadProgramInfo(conn, TEST_ID);
        assertNotNull(pi);
        assertTrue(pi.getTestDefId() > 0);
    }
 }
