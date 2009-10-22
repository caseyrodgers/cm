package hotmath.testset.ha.info;

import hotmath.gwt.cm.server.CmDbTestCase;

public class UserProgramInfo_Test extends CmDbTestCase {
    
    public UserProgramInfo_Test(String name){
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        
        if(_testRun == null)
            setupDemoAccountTestRun();
    }
    
    public void testCreate() throws Exception {
        UserProgramInfo upi = new UserProgramInfoDao().getUserProgramInfo(conn, _test.getProgramInfo().getId());
        assertTrue(upi.getTests().size() > 0);
        
        assertTrue(upi.getTests().get(0).getTestRuns().size() > 0);
        
        assertTrue(upi.getTests().get(0).getTestRuns().get(0).getRunTime() > 0);
    }
}
