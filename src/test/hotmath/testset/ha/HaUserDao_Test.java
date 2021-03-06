package hotmath.testset.ha;

import hotmath.cm.login.ClientEnvironment;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.UserTutorWidgetStats;

public class HaUserDao_Test extends CmDbTestCase {
    
    public HaUserDao_Test(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_testRun == null) {
            setupDemoAccountTestRun();
        }
    }

    public void testReadByIdNoCache() throws Exception {
        HaUser user = HaUserDao.getInstance().lookUser(_user.getUid(), false);
        assertTrue(user != null);
    }
    
    public void testClientEnvironment() throws Exception {
        ClientEnvironment ce = HaUserDao.getInstance().getLatestClientEnvironment(_user.getUserKey());
        assert(ce.isFlashEnabled());
    }
    
    public void testSaveUserTutorInputWidget() throws Exception {
        UserTutorWidgetStats stats = HaUserDao.getInstance().saveUserTutorInputWidgetAnswer(_user.getUserKey(), _testRun.getRunId(),_test.getPids().get(0),"value",false);
        assertTrue(stats.getCorrectPercent() > -1);
    }

    public void testGetUserActiveTimeForDateRange() throws Exception {
        int activeTime = HaUserDao.getInstance().getUserActiveTimeForDateRange(12345, null, null);
        assertTrue(activeTime >= 0);
    }
}
