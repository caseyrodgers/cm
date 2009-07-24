package hotmath.gwt.cm.server;

import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.testset.ha.HaUserFactory;

public class CmDbTestCase extends DbTestCase {
    
    public CmDbTestCase(String name) {
        super(name);
    }
    
    
    /** Create a new test account to use for test/samples/demo
     * 
     * @return The userid of the test account
     * 
     * @throws Exception
     */
    public int setupDemoAccount() throws Exception {
        HaBasicUser user = HaUserFactory.createDemoUser();

        new CmStudentDao().assignProgramToStudent(user.getUserKey(),"Pre-Alg", "Prof",null);
        
        return user.getUserKey();
    }
}
