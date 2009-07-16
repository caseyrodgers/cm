package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm.server.DbTestCase;
import hotmath.gwt.cm_tools.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentUserProgramModel;

public class CmAdminDao_Test extends DbTestCase {

    /**
     * @TODO: create method to generate test accounts for testing
     * 
     */
    static final int TEST_ID = 700;

    public CmAdminDao_Test(String name) throws Exception {
        super(name);
    }

    CmStudentDao _dao;

    protected void setUp() throws Exception {
        super.setUp();
        _dao = new CmStudentDao();
    }

    public void testSetStudentProgram() throws Exception {

        /**
         * change it once to Alg 2
         * 
         */
        StudentModel sm = _dao.getStudentModel(TEST_ID);
        sm.setProgId("Prof");
        sm.setSubjId("Alg 2");
        sm.setProgramChanged(true);
        _dao.updateStudent(sm, true, false, true, false);

        StudentUserProgramModel progInfo = _dao.loadProgramInfo(conn, TEST_ID);
        assertTrue(progInfo.getTestName().equals("Algebra 2 Proficiency"));

        
        /**
         * change it again to Alg 1
         * 
         */
        sm = _dao.getStudentModel(TEST_ID);
        sm.setProgId("Prof");
        sm.setSubjId("Alg 1");
        sm.setProgramChanged(true);
        _dao.updateStudent(sm, true, false, true, false);

        progInfo = _dao.loadProgramInfo(conn, TEST_ID);
        assertTrue(progInfo.getTestName().equals("Algebra 1 Proficiency"));
    }

    public void testLoadStudentUserProgramModel() throws Exception {
        StudentUserProgramModel up = _dao.loadProgramInfo(conn, TEST_ID);
        assertNotNull(up);

        assertTrue(up.getTestDefId() > 0);
        assertNotNull(up.getTestName());
    }

    public void testStudentActiveInfo() throws Exception {
        StudentActiveInfo activeInfo = _dao.loadActiveInfo(conn, TEST_ID);
        assertNotNull(activeInfo);
        assertTrue(activeInfo.getActiveTestId() > 0);
    }

    public void testRestStudentActiveInfo() throws Exception {
        StudentActiveInfo activeInfo = new StudentActiveInfo();
        activeInfo.setActiveTestId(100);
        _dao.setActiveInfo(conn, TEST_ID, activeInfo);
    }

}
