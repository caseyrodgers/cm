package hotmath.testset.ha;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;

import java.util.List;

public class EndOfProgramHander_Test extends CmDbTestCase {
    
    CmStudentDao dao;
    public EndOfProgramHander_Test(String name) {
        super(name);
    }
    
    int userId=0;
    protected void setUp() throws Exception {
        super.setUp();
        
        dao = new CmStudentDao();
        userId = setupDemoAccount();
    }
    
    public void testEndOfProgramChapterTestGeoChap() throws Exception {

        // assign chapter test to test student (last chapter)
        dao.assignProgramToStudent(conn,userId, CmProgram.GEOM_CHAP, "Circles");
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should be moved to the Pre-Alg proficiency test
        assertTrue(nextProgram.getTestDefId() == CmProgram.GEOM_CHAP.getDefId());
        assertTrue(nextProgram.getConfig().getChapters().get(0).equals("Polygons and Areas of Plane Figures"));
    }  
    
    
    public void testEndOfProgramChapterTestGeoChap2Prof() throws Exception {

        HaTestDefDao tda = new HaTestDefDao();
        List<String> chaps = tda.getProgramChapters(conn, tda.getTestDef(conn, CmProgram.GEOM_CHAP.getDefId()));
        
        String lastChap = chaps.get(chaps.size()-1);
        
        // assign chapter test to test student (last chapter)
        dao.assignProgramToStudent(conn,userId, CmProgram.GEOM_CHAP, lastChap);
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should be moved to the Pre-Alg proficiency test
        assertTrue(nextProgram.getTestDefId() == CmProgram.GEOM_PROF.getDefId());
    }
    
    public void testEndOfProgramChapterTestTaks() throws Exception {

        // assign chapter test to test student (last chapter)
        dao.assignProgramToStudent(conn, userId, CmProgram.TAKS, null);
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should just loop around
        assertTrue(nextProgram.getTestDefId() == CmProgram.TAKS.getDefId());
    }  
    
    
    public void testEndOfProgramChapterTestCahseehm() throws Exception {

        // assign chapter test to test student (last chapter)
        dao.assignProgramToStudent(conn, userId, CmProgram.CAHSEEHM, null);
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should just loop around
        assertTrue(nextProgram.getTestDefId() == CmProgram.CAHSEEHM.getDefId());
    }  
    
    
    public void testEndOfProgramChapterTestGeo() throws Exception {

        // assign chapter test to test student (last chapter)
        dao.assignProgramToStudent(conn,userId, CmProgram.GEOM_PROF, null);
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should just loop around
        assertTrue(nextProgram.getTestDefId() == CmProgram.ALG2_PROF.getDefId());
    }     
    
    public void testEndOfProgramChapterTesAlg1Chap() throws Exception {

        // assign chapter test to test student (last chapter)
        dao.assignProgramToStudent(conn,userId, CmProgram.ALG1_CHAP, "Solving Linear Equations");
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should be moved to the Pre-Alg proficiency test
        assertTrue(nextProgram.getTestDefId() == CmProgram.ALG1_CHAP.getDefId());
        HaTestConfig testConfig = nextProgram.getConfig();
        assertTrue(testConfig != null);
        assertTrue(testConfig.getChapters().size() > 0);
    }  
    
    
    public void testEndOfProgramChapterTestPreAlgChap() throws Exception {

        // assign chapter test to test student (last chapter)
        dao.assignProgramToStudent(conn,userId, CmProgram.PREALG_CHAP, "Linear Equations and Inequalities");
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should be moved to the Pre-Alg proficiency test
        assertTrue(nextProgram.getTestDefId() == CmProgram.PREALG_PROF.getDefId());
        HaTestConfig testConfig = nextProgram.getConfig();
        assertTrue(testConfig != null);
        assertTrue(testConfig.getChapters().size() == 0);
    }  

    
    public void testEndOfProgramChapterTest() throws Exception {

        // assign chapter test to test student
        dao.assignProgramToStudent(conn,userId, CmProgram.PREALG_CHAP, "Integers");

        // read currently set program info
        StudentUserProgramModel currProgram = new CmUserProgramDao().loadProgramInfoCurrent(conn,userId);
        
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        
        // this should be the next chapter in the current prog/subj
        assertTrue(nextProgram.getTestDefId() == currProgram.getTestDefId());
        HaTestConfig testConfig = nextProgram.getConfig();
        assertTrue(testConfig != null);
        assertTrue(testConfig.getChapters().size() > 0);
        
        // next chapter must be Number Theory
        assertTrue(testConfig.getChapters().get(0).equals("Number Theory"));
    }  

    /** Be sure to remove user
     * 
     */
    protected void tearDown() throws Exception {
        CmStudentDao dao = new CmStudentDao();
        dao.removeUser(conn,dao.getStudentModel(userId));
    }
    
    
    /** Just make sure can create
     * 
     * @throws Exception
     */
    public void testEndOfProgram() throws Exception {
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        assertNotNull(eop);
    }
    

    /** Make sure new program is assigned
     * 
     * @throws Exception
     */
    public void testEndOfProgramAssign() throws Exception {
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        assertNotNull(nextProgram);
        
        assertNotNull(nextProgram.getTestName());
    }
    
    
    /** make sure new program is different than current
     * 
     * @throws Exception
     */
    public void testEndOfProgramAssignValid() throws Exception {
        
        // get curr
        StudentUserProgramModel currProgram = new CmUserProgramDao().loadProgramInfoCurrent(conn,userId);
        
        // auto assign next
        EndOfProgramHandler eop = new EndOfProgramHandler(userId);
        StudentUserProgramModel nextProgram = eop.getNextProgram();
        assertNotNull(nextProgram);
        
        // make sure it was actually updated
        assertTrue(nextProgram.getTestName() != currProgram.getTestName());
    }   
    

    
}
