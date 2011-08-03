package hotmath.testset.ha;

import hotmath.gwt.cm.server.CmDbTestCase;

import java.util.List;

public class HaTest_Test extends CmDbTestCase {

    String CHAP_TEST, PROF_TEST;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (_test == null)
            setupDemoAccountTest();

        CHAP_TEST = HaTestDefDao.getInstance().getTestDef(CmProgram.ALG1_CHAP.getDefId()).getName();
        PROF_TEST = HaTestDefDao.getInstance().getTestDef(CmProgram.ALG1_PROF.getDefId()).getName();
    }

    StudentUserProgramModel userProgram = new StudentUserProgramModel();

    public HaTest_Test(String name) throws Exception {
        super(name);
    }

    public void testCreate1() throws Exception {
        HaTest test = HaTestDao.getInstance().createTest(_test.getUser().getUid(), HaTestDefDao.getInstance().getTestDef(
                CHAP_TEST), 1);
        assertTrue(test != null);
        assertTrue(test.getGradeLevel() > 0);
    }

    public void testCreateTestRun() throws Exception {
        HaTest test = HaTestDao.getInstance().loadTest( _test.getTestId());
        String pids[] = { "pid_1", "pid_2" };
        HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn, test.getUser().getUid(), test.getTestId(), 0, 0, 0);
        assertNotNull(testRun);

        assertTrue(testRun.getTestRunResults().size() > 0);

        HaTestRun run = HaTestRunDao.getInstance().lookupTestRun(testRun.getRunId());
        String pidss = run.getPidList();
        assertNotNull(pidss);
    }

    public void testCreateTestDef() throws Exception {
        HaTestDef htd = HaTestDefDao.getInstance().getTestDef(PROF_TEST);
        
        HaTest test = HaTestDao.getInstance().loadTest( _test.getTestId());
        assertTrue(htd.getTestIdsForSegment(test.getProgramInfo(), 2, new HaTestConfig(null), 0).size() > 0);
    }

    public void testCreateTestDef4() throws Exception {
        HaTestDef htd = HaTestDefDao.getInstance().getTestDef(PROF_TEST);
        assertTrue(htd.getTestIdsForSegment(userProgram, 4, new HaTestConfig(null), 0).size() > 0);
    }

    public void testLookupTestRun1() throws Exception {
        HaTest test = HaTestDao.getInstance().loadTest( _test.getTestId());
        HaTestConfig config = test.getProgramInfo().getConfig();
        assertNotNull(config);
    }

    public void testGetHaTestDefChapters() throws Exception {
        HaTestDefDao dao = HaTestDefDao.getInstance();
        List<String> chapters = dao.getProgramChapters(conn, dao.getTestDef(CHAP_TEST));
        assertTrue(chapters.size() > 0);
    }

    public void testGetTitle() throws Exception {
        _test = HaTestDao.getInstance().loadTest( _test.getTestId());
        assertNotNull(_test.getTitle());
    }

    public void testQuestionAnswerChanged() throws Exception {
        HaTest htd = HaTestDao.getInstance().loadTest( _test.getTestId());
        HaTestDao.saveTestQuestionChange(conn, _test.getTestId(), "TEST", 1, true);
    }

    public void testGetChapterTest() throws Exception {

        HaTest test = HaTestDao.getInstance().createTest(_test.getUser().getUid(), HaTestDefDao.getInstance().getTestDef(
                CHAP_TEST), 1);
        assertNotNull(test);
    }

    /**
     * Make sure always returns in same order
     * 
     * @throws Exception
     */
    public void testCreateTestDefOrder() throws Exception {
        HaTestDef htd1 = HaTestDefDao.getInstance().getTestDef(CHAP_TEST);

        List<String> list1 = htd1.getTestIdsForSegment(_test.getProgramInfo(), 1, null, 0);
        List<String> list2 = htd1.getTestIdsForSegment(_test.getProgramInfo(), 1, null, 0);

        assertTrue(list1.get(5).equals(list2.get(5)));
    }

    public void testCreate() throws Exception {
        HaTest test = HaTestDao.getInstance().createTest( _test.getUser().getUid(), HaTestDefDao.getInstance().getTestDef(
                CHAP_TEST), 1);
        assertTrue(test != null);
    }

    public void testLookup() throws Exception {
        HaTest test = HaTestDao.getInstance().loadTest( _test.getTestId());
        assertTrue(test != null);
        assertTrue(test.getPids().size() > 0);
    }

}
