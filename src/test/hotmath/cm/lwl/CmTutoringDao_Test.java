package hotmath.cm.lwl;

import hotmath.gwt.cm.server.CmDbTestCase;

public class CmTutoringDao_Test extends CmDbTestCase {
    public CmTutoringDao_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null)
            setupDemoAccount();
        
        new CmTutoringDao().addTutoring(conn, _user.getUid());
    }
    
    public void testGetTutoringInfo() throws Exception {
        StudentTutoringInfo tutoring = new CmTutoringDao().getStudentTutoringInfo(conn, _user.getUid());
        assertNotNull(tutoring);
        
        assertNotNull(tutoring.getSubscriberId());
        assertTrue(tutoring.getStudentNumber() > 0);
        assertTrue(tutoring.getSchoolNumber() > 0);
    }
}
