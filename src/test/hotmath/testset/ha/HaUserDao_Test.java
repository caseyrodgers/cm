package hotmath.testset.ha;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.server.service.CmTestUtils;

public class HaUserDao_Test extends CmDbTestCase {
    
    public HaUserDao_Test(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null) {
            setupDemoAccount();
        }
    }
    public void testReadByIdNoCache() throws Exception {
        HaUser user = HaUserDao.getInstance().lookUser(_user.getUid(), false);
        assertTrue(user != null);
    }
}
