package hotmath.testset.ha;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import junit.framework.TestCase;

public class HaUserFactory_Test extends TestCase {
    
    public HaUserFactory_Test(String name) {
        super(name);
    }

    
    public void testcreateAnonymousUser() throws Exception {
        HaBasicUser basicUser = HaUserFactory.createAnonymousUser("topics/integers.html");
        assertNotNull(basicUser);
        assertNotNull(basicUser.getUserKey() > 0);
    }

    
    public void testLookup() throws Exception {
        HaBasicUser basicUser = HaUserFactory.loginToCatchup("casey","casey",HaBasicUser.UserType.STUDENT);
    }

    public void testLookupAdmin() throws Exception {
        HaBasicUser basicUser = HaUserFactory.loginToCatchup("casey","casey", HaBasicUser.UserType.STUDENT);
        
        assertTrue(basicUser.getUserType() ==  HaBasicUser.UserType.ADMIN);
    }

    
    public void testLookupStudent() throws Exception {
        HaBasicUser basicUser = HaUserFactory.loginToCatchup("casey","t1", HaBasicUser.UserType.STUDENT);
        
        assertTrue(basicUser.getUserType() ==  HaBasicUser.UserType.STUDENT);
    }


    public void testLookupFail() throws Exception {
        
        try {
            HaUserFactory.loginToCatchup("232323","DOESNOTEXIST",HaBasicUser.UserType.STUDENT);
            assertTrue(false); // should not be here.
        }
        catch(Exception e) {
            // ok
        }
    }

}
