package hotmath.cm.server.rest;

import hotmath.gwt.cm_mobile_shared.client.rpc.GetCmMobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.CheckCm2QuizAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2MobileLoginAction;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaUserDao;

/** Central place to request a CM2 request
 *  with any specialized formatting required.
 *  
 *  serves as a wrapper around ActionDispatcher actions.
 *  
 * @author casey
 *
 */
public class Cm2ActionManager {

    
    /** Wrapper around GetUserInfoAction
     * 
     *  process login info and returns composite data
     *  with either relevant quiz or prescription info.
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public static String getUserCurrentProgram(int userId) throws Exception {
        
        
        // int randomTestId = HaTestDao.getRandomTestId();
        int randomUserId = HaUserDao.getRandomUserId();
        //int randomRunId = HaTestRunDao.getRandomTestRun();
        
        
        GetCmMobileLoginAction action = new GetCmMobileLoginAction(randomUserId);
        // GetUserInfoAction action = new GetUserInfoAction(randomUserId, "Random Test");
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        // GetCm2QuizHtmlAction action = new GetCm2QuizHtmlAction(randomTestId);
        
        return jsonResponse;
    }


    //static int TEST_ID=2610241;  // debug
    static int USER_ID = 678549;
    public static String checkQuiz(int testId) throws Exception {
        
        HaTestDao.resetTest(testId);
            
        
        CheckCm2QuizAction action = new CheckCm2QuizAction(testId);
        
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        return jsonResponse;
    }
    
    public static String loginUser(int uid, String un, String pwd) throws Exception {
        // int randomUserId = HaUserDao.getRandomUserId();
        GetCm2MobileLoginAction action = uid!=0?new GetCm2MobileLoginAction(uid): new GetCm2MobileLoginAction(un, pwd);
        String jsonResponse = new ActionDispacherWrapper().execute(action);
        return jsonResponse;
    }

}
