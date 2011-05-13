package hotmath.testset.ha;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;

import java.util.List;

public class HaTestDefDao_Test extends CmDbTestCase {

    public HaTestDefDao_Test(String name) {
        super(name);
    }

    HaTestDefDao dao;
    
    StudentUserProgramModel userProgram = new StudentUserProgramModel();

    protected void setUp() throws Exception {
        super.setUp();
        dao = HaTestDefDao.getInstance();
    }
    
    
    public void testGetSubTitle3() throws Exception {
        int uid = setupDemoAccount();
        
        CmStudentDao dao = CmStudentDao.getInstance();
        dao.assignProgramToStudent(conn,uid, CmProgram.ALG2_CHAP, "Quadratic Equations");
        StudentUserProgramModel spi = CmUserProgramDao.getInstance().loadProgramInfoCurrent(uid);
        
        HaTestDefDao testDefDao = HaTestDefDao.getInstance();
        ChapterInfo chapterInfo = testDefDao.getChapterInfo(conn,spi);
        assertNotNull(chapterInfo);  // make sure has chapter number
        assertTrue(chapterInfo.getChapterNumber() > 0);
    }    
    
    public void testGetSubTitle2() throws Exception {
        int uid = setupDemoAccount();
        
        CmStudentDao dao = CmStudentDao.getInstance();
        dao.assignProgramToStudent(conn,uid, CmProgram.PREALG_CHAP, "Integers");
        StudentUserProgramModel spi = CmUserProgramDao.getInstance().loadProgramInfoCurrent(uid);
        
        HaTestDefDao testDefDao = HaTestDefDao.getInstance();
        ChapterInfo chapterInfo = testDefDao.getChapterInfo(conn,spi);
        assertNotNull(chapterInfo);  // make sure has chapter number
        assertTrue(chapterInfo.getChapterNumber() > 0);
    }
    
    
    public void testGetSubTitle() throws Exception {
        int uid = setupDemoAccount();
        
        CmStudentDao dao = CmStudentDao.getInstance();
        StudentUserProgramModel spi = CmUserProgramDao.getInstance().loadProgramInfoCurrent(uid);
        
        HaTestDefDao testDefDao = HaTestDefDao.getInstance();
        
        
        ChapterInfo ci = testDefDao.getChapterInfo(conn,spi);
        assertTrue(ci == null);
    }

    public void testGetTestDefById() throws Exception {
        HaTestDef testDef = dao.getTestDef(16);
        assertNotNull(testDef);
    }

    public void testGetTestDefByName() throws Exception {
        HaTestDef testDef = dao.getTestDef("Algebra 1 Proficiency");
        assertNotNull(testDef);
    }

    public void testCountDistinctTests() throws Exception {
        List<String> names = dao.getTestNames(conn);
        assertTrue(names.size() > 0);
    }

    public void testGetTestIdsForFirstDef() throws Exception {
        List<String> names = dao.getTestNames(conn);
        HaTestDef def = dao.getTestDef(names.get(0));
        HaTestConfig config = new HaTestConfig(null);
        List<String> pids = dao.getTestIdsForSegment(userProgram,1, def.getTextCode(), def.chapter, config,0);
        assertTrue(pids.size() > 0);

    }

    public void testGetProgramChapters() throws Exception {
        List<String> names = dao.getTestNames(conn);
        HaTestDef def = dao.getTestDef(names.get(0));
        List<String> chapters = dao.getProgramChapters(conn, def);
        assertTrue(chapters.size() > 0);
    }
}
