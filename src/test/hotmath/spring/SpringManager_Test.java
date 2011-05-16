package hotmath.spring;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.conn.TestDao;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_admin.server.model.CmHighlightsDao;
import hotmath.gwt.cm_admin.server.model.CmQuizzesDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUserDao;
import junit.framework.TestCase;

public class SpringManager_Test extends TestCase{
    
    public SpringManager_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        assertNotNull(manager);
    }
    
    public void testCreateTestDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        TestDao testDao = (TestDao)manager.getBeanFactory().getBean("testDao");
        assertNotNull(testDao);
    }
    
    public void testCreateCmUserProgramDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmUserProgramDao testDao = (CmUserProgramDao)manager.getBeanFactory().getBean("cmUserProgramDao");
        assertNotNull(testDao);
    }    
    
    public void testCreateCmStudentDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmStudentDao testDao = (CmStudentDao)manager.getBeanFactory().getBean("cmStudentDao");
        assertNotNull(testDao);
    }    

    public void testCreateHaTestDefDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        HaTestDefDao testDao = (HaTestDefDao)manager.getBeanFactory().getBean("haTestDefDao");
        assertNotNull(testDao);
    }        

    public void testCreateCmAdminDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmAdminDao testDao = (CmAdminDao)manager.getBeanFactory().getBean("cmAdminDao");
        assertNotNull(testDao);
    }
    
    public void testCreateHaTestDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        HaTestDao testDao = (HaTestDao)manager.getBeanFactory().getBean("haTestDao");
        assertNotNull(testDao);
    }
    
    public void testCreateHaTestRunDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        HaTestRunDao testDao = (HaTestRunDao)manager.getBeanFactory().getBean("haTestRunDao");
        assertNotNull(testDao);
    }    
    
    public void testCreateHaUserDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        HaUserDao testDao = (HaUserDao)manager.getBeanFactory().getBean("haUserDao");
        assertNotNull(testDao);
    }
    
    public void testCmCustomProgramDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmCustomProgramDao testDao = (CmCustomProgramDao)manager.getBeanFactory().getBean("cmCustomProgramDao");
        assertNotNull(testDao);
    }
    
    public void testCmQuizzesDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmQuizzesDao testDao = (CmQuizzesDao)manager.getBeanFactory().getBean("cmQuizzesDao");
        assertNotNull(testDao);
    }
    
    public void testCmHighlightsDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmHighlightsDao testDao = (CmHighlightsDao)manager.getBeanFactory().getBean("cmHighlightsDao");
        assertNotNull(testDao);
    }    
}
