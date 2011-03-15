package hotmath.testset.ha;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaLoginInfo;

public class HaLoginInfo_Test extends CmDbTestCase {
    
    public HaLoginInfo_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        
        HaBasicUser user = HaUserFactory.loginToCatchup("casey_test1","casey_test1");
        HaLoginInfo info = new HaLoginInfoDao().getLoginInfo(conn, user);
        
        assertTrue(info != null);
    }

}
