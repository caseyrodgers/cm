package hotmath.cm.lwl;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;

public class CmTutoringDao_Test extends CmDbTestCase {
    public CmTutoringDao_Test(String name) {
        super(name);
    }
    
    int uid=0;
    StudentModel student;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        uid = setupDemoAccount();
        new CmTutoringDao().addTutoring(conn, uid);
    }
    
    @Override
    protected void tearDown() throws Exception {
        new CmStudentDao().removeUser(conn, new CmStudentDao().getStudentModel(uid));
    }
    
    public void testGetTutoringInfo() throws Exception {
        StudentTutoringInfo tutoring = new CmTutoringDao().getStudentTutoringInfo(conn, uid);
        assertNotNull(tutoring);
        
        assertNotNull(tutoring.getSubscriberId());
        assertTrue(tutoring.getLwlId() > 0);
    }
}
