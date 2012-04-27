package hotmath.spring;

import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.server.model.CmReportCardDao;
import hotmath.cm.server.model.CmTemplateDao;
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
import hotmath.testset.ha.HaTestRunHomeWorkDao;
import hotmath.testset.ha.HaUserDao;
import hotmath.testset.ha.HaUserExtendedDao;
import hotmath.testset.ha.SolutionDao;
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
        CmUserProgramDao testDao = (CmUserProgramDao)manager.getBeanFactory().getBean(CmUserProgramDao.class.getName());
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
    
    public void testCmExtendedDataDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        HaUserExtendedDao testDao = (HaUserExtendedDao)manager.getBeanFactory().getBean("hotmath.testset.ha.HaUserExtendedDao");
        assertNotNull(testDao);
    }
    
    public void testCmReportCardDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmReportCardDao testDao = (CmReportCardDao)manager.getBeanFactory().getBean("hotmath.cm.server.model.CmReportCardDao");
        assertNotNull(testDao);
    }
    
    public void testHaLoginInfoDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        HaLoginInfoDao testDao = (HaLoginInfoDao)manager.getBeanFactory().getBean("hotmath.cm.dao.HaLoginInfoDao");
        assertNotNull(testDao);
    }
    
    public void testParallelProgramDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        assertNotNull(manager.getBeanFactory().getBean("parallelProgramDao"));
    }  
    
    public void testCmTemplateDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        CmTemplateDao testDao = (CmTemplateDao)manager.getBeanFactory().getBean("hotmath.cm.server.model.CmTemplateDao");
        assertNotNull(testDao);
    }
    
    public void testCmHomeWorkDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        HaTestRunHomeWorkDao testDao = (HaTestRunHomeWorkDao)manager.getBeanFactory().getBean("hotmath.testset.ha.HaTestRunHomeWorkDao");
        assertNotNull(testDao);
    }
    
    public void testSolutionDao() throws Exception {
        SpringManager manager = SpringManager.getInstance();
        SolutionDao testDao = (SolutionDao)manager.getBeanFactory().getBean("hotmath.testset.ha.SolutionDao");
        assertNotNull(testDao);
    }

}
