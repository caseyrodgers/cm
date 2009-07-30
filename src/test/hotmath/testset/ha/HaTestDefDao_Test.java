package hotmath.testset.ha;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;

import java.util.List;

public class HaTestDefDao_Test extends CmDbTestCase {

    public HaTestDefDao_Test(String name) {
        super(name);
    }

    HaTestDefDao dao;

    protected void setUp() throws Exception {
        super.setUp();
        dao = new HaTestDefDao();
    }
    
    
    public void testGetSubTitle2() throws Exception {
        int uid = setupDemoAccount();
        
        CmStudentDao dao = new CmStudentDao();
        dao.assignProgramToStudent(conn,uid, CmProgram.PREALG_CHAP,"Integers");
        StudentUserProgramModel spi = dao.loadProgramInfo(conn,uid);
        
        HaTestDefDao testDefDao = new HaTestDefDao();
        ChapterInfo chapterInfo = testDefDao.getChapterInfo(conn,spi);
        assertNotNull(chapterInfo);  // make sure has chapter number
        assertTrue(chapterInfo.getChapterNumber() > 0);
    }
    
    
    public void testGetSubTitle() throws Exception {
        int uid = setupDemoAccount();
        
        CmStudentDao dao = new CmStudentDao();
        StudentUserProgramModel spi = dao.loadProgramInfo(conn,uid);
        
        HaTestDefDao testDefDao = new HaTestDefDao();
        
        
        ChapterInfo ci = testDefDao.getChapterInfo(conn,spi);
        assertTrue(ci == null);
    }

    public void testGetTestDefById() throws Exception {
        HaTestDef testDef = dao.getTestDef(conn, 16);
        assertNotNull(testDef);
    }

    public void testCountDistinctTests() throws Exception {
        List<String> names = dao.getTestNames();
        assertTrue(names.size() > 0);
    }

    public void testGetTestIdsForFirstDef() throws Exception {
        List<String> names = dao.getTestNames();
        HaTestDef def = dao.getTestDef(conn, names.get(0));
        HaTestConfig config = new HaTestConfig(null);
        List<String> pids = dao.getTestIdsForSegment(conn, 1, def.getTextCode(), def.chapter, config);
        assertTrue(pids.size() > 0);

    }

    public void testGetProgramChapters() throws Exception {
        List<String> names = dao.getTestNames();
        HaTestDef def = dao.getTestDef(conn, names.get(0));
        List<String> chapters = dao.getProgramChapters(def);
        assertTrue(chapters.size() > 0);
    }
}
