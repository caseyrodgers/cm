package hotmath.cm.dao;

import hotmath.cm.login.ClientEnvironment;
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
        HaLoginInfo info = HaLoginInfoDao.getInstance().getLoginInfo(conn, user, new ClientEnvironment(), false);
        
        assertTrue(info.getKey() != null);
    }
    
    public void testGetLoginKey() throws Exception {
        String key = HaLoginInfoDao.getInstance().getLatestLoginKey(_user.getUid());
        assertTrue(key != null);
        
        key = HaLoginInfoDao.getInstance().getLatestLoginKey(-123456);
        assertTrue(key == null);

    }
    
}
