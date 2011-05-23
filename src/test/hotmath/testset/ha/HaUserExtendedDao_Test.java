package hotmath.testset.ha;

import hotmath.gwt.cm.server.CmDbTestCase;

import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaUserExtendedDao;

public class HaUserExtendedDao_Test extends CmDbTestCase {

    HaUserExtendedDao dao;
    SpringManager manager;
    
    public HaUserExtendedDao_Test(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        manager = SpringManager.getInstance();
        dao = (HaUserExtendedDao)manager.getBeanFactory().getBean("hotmath.testset.ha.HaUserExtendedDao");
    }
    
    public void testResyncUserExtendedLessonStatusForUid() throws Exception {
        int uid = setupDemoAccount();
        
        dao.resyncUserExtendedLessonStatusForUid(uid);
        assertTrue(true);
    }    
    

}
