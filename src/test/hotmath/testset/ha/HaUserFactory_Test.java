package hotmath.testset.ha;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import junit.framework.TestCase;

public class HaUserFactory_Test extends TestCase {
    
    public HaUserFactory_Test(String name) {
        super(name);
    }

    
    public void testLookup() throws Exception {
        HaBasicUser basicUser = HaUserFactory.loginToCatchup("casey_test1","casey_test1");
    }

    public void testLookupAdmin() throws Exception {
        HaBasicUser basicUser = HaUserFactory.loginToCatchup("casey","casey");
        
        assertTrue(basicUser.getUserType() ==  HaBasicUser.UserType.ADMIN);
    }

    
    public void testLookupStudent() throws Exception {
        HaBasicUser basicUser = HaUserFactory.loginToCatchup("casey_test1","casey_test1");
        
        assertTrue(basicUser.getUserType() ==  HaBasicUser.UserType.STUDENT);
    }


    public void testLookupFail() throws Exception {
        
        try {
            HaUserFactory.loginToCatchup("232323","DOESNOTEXIST");
            assertTrue(false); // should not be here.
        }
        catch(Exception e) {
            // ok
        }
    }

}
