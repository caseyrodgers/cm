package hotmath.testset.ha.info;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.Date;
import java.util.List;

public class UserProgramInfoDao_Test extends CmDbTestCase {
    
    public UserProgramInfoDao_Test(String name) throws Exception {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();

        if(_test == null){
            setupDemoAccountTest();
        }
    }

    public void testGetUserProgInfo() throws Exception {
        List<UserProgramInfo> upis = new UserProgramInfoDao().getUserProgramInfoForUser(conn, _user.getUid());
        assertTrue(upis.size() > 0);
    }
    
    public void testGetInfo() throws Exception {
        UserProgramInfo upi = new UserProgramInfoDao().getUserProgramInfo(conn, _test.getProgramInfo().getId());
        assertTrue(upi.getTests().size() > 0);
    }
    
    public void testGetUserInfos() throws Exception {
        List<UserInfo> uis = new UserProgramInfoDao().getUserProgramInfoForAdmin(conn, _user.getAid());
        assertTrue(uis.size() > 0);
    }

    public void testGetUserFirstActivityDate() throws Exception {
        Date firstAct = new UserProgramInfoDao().getUserFirstActivityDate(conn, _user.getUid());
        assertNotNull(firstAct);
    }
}
