package hotmath.cm.server.rest;

import hotmath.testset.ha.HaTestDao;
import junit.framework.TestCase;

public class Cm2ActionManager_Test extends TestCase {

    
    int TEST_ID=2610241;
    int USER_ID = 678563;
    int RUN_ID=3650503;
    public Cm2ActionManager_Test(String name) {
        super(name);
    }
    
    public void testGetSearchTopic() throws Exception {
    	String result = Cm2ActionManager.getSearchTopic("topics/integers.html");
    	assertNotNull(result);
    }

    public void testSearch() throws Exception {
        String searchResults = Cm2ActionManager.getSearchResults(0, "integers");
        assertTrue(searchResults != null);
    }

    public void testLogin1() throws Exception {
        String userInfo = Cm2ActionManager.loginUser(USER_ID,null,null,null);
        assertTrue(userInfo != null);
    }
    
    public void testLogin2() throws Exception {
        String userInfo = Cm2ActionManager.getUserCurrentProgram(USER_ID);
        assertTrue(userInfo != null);
    }

    public void testCheckQuiz() throws Exception {
        HaTestDao.resetTest(TEST_ID);
        
        String result = Cm2ActionManager.checkQuiz(TEST_ID);
        assertTrue(result != null);
    }
    
    public void testGetQuiz() throws Exception {
        HaTestDao.resetTest(TEST_ID);
        
        String result = Cm2ActionManager.getUserCurrentProgram(USER_ID);
        assertTrue(result != null);
    }
    
    public void testGetPrescription() throws Exception {
        Cm2ActionManager.getPrescriptionTopic(3650503, 0);
    }

}
