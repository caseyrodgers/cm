package hotmath.testset.ha;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;

public class HaLoginInfo_Test extends CmDbTestCase {
    
    public HaLoginInfo_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        
        HaBasicUser user = HaUserFactory.loginToCatchup("casey","casey");
        HaLoginInfo info = HaLoginInfoDao.getInstance().getLoginInfo(conn, user, new ClientEnvironment(),false);
        
        assertTrue(info != null);
    }

}
