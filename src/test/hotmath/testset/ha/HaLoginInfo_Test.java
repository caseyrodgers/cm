package hotmath.testset.ha;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import junit.framework.TestCase;

public class HaLoginInfo_Test extends TestCase {
    
    public HaLoginInfo_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        
        HaBasicUser user = HaUserFactory.loginToCatchup("casey_test1","casey_test1");
        HaLoginInfo info = new HaLoginInfo(user);
        
        assertTrue(info != null);
    }

}
