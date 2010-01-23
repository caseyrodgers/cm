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
    
    public void testGetTutoringSessionCount() throws Exception {
        
    }
    
    public void testGetTutoringInfo() throws Exception {
        int availMins = new CmTutoringDao().getTutoringSessionCount(conn, _user.getUid());
        assertTrue(availMins > 0);
    }
}
