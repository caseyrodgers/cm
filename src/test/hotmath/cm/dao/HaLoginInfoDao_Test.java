package hotmath.cm.dao;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;
import hotmath.testset.ha.HaUser;

public class HaLoginInfoDao_Test extends CmDbTestCase {
    
    public HaLoginInfoDao_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null)
            setupDemoAccount();
    }
    
    public void testGetLogin() throws Exception {
        HaUser user = HaUser.lookupUserByPasscode(conn, _user.getPassword());
        HaLoginInfo info = new HaLoginInfoDao().getLoginInfo(conn, user);
        
        assertTrue(info.getKey() != null);
    }
    
}
