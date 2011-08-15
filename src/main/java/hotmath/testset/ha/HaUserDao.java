package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * defines data access methods for HaTest
 * 
 * @author Bob
 * 
 */

public class HaUserDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(HaUserDao.class);

    static private HaUserDao __instance;

    static public HaUserDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (HaUserDao) SpringManager.getInstance().getBeanFactory().getBean("haUserDao");
        }
        return __instance;
    }

    private HaUserDao() {
        /** empty */
    }

    public HaUser lookUser(Integer uid, boolean cachedOk) throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {

            HaUser user;
            String cacheKey = String.valueOf(uid);
            if (cachedOk) {
                /**
                 * Problem here is possible changes to the USER since last put
                 * in cache By default cache is not used; client should only use
                 * cache if possible staleness is OK Currently, life of HA_USER
                 * cache is 5 minutes.
                 */

                user = (HaUser) CmCacheManager.getInstance().retrieveFromCache(CacheName.HA_USER, cacheKey);
                if (user != null)
                    return user;
            }

            String sql = CmMultiLinePropertyReader.getInstance().getProperty("HA_USER_LOOKUP_USER",
                    new String[] { "WHERE_CLAUSE|where u.uid = ?" });

            user = getJdbcTemplate().queryForObject(sql, new Object[] { uid }, new RowMapper<HaUser>() {
                @Override
                public HaUser mapRow(ResultSet rs, int rowNum) throws SQLException {

                    HaUser user = new HaUser();
                    user.setUid(rs.getInt("uid"));
                    user.setUserName(rs.getString("user_name"));
                    user.setAssignedTestName(rs.getString("assigned_test_name"));
                    user.setActiveTestSegment(rs.getInt("active_segment"));
                    user.setActiveTest(rs.getInt("active_test_id"));
                    user.setActiveTestRunId(rs.getInt("active_run_id"));
                    user.setActiveTestRunSession(rs.getInt("active_run_session"));
                    user.setTestConfigJson(rs.getString("test_config_json"));
                    user.setBackgroundStyle(rs.getString("gui_background_style"));
                    user.setShowWorkRequired(rs.getInt("is_show_work_required") == 0 ? false : true);
                    user.setPassPercentRequired(rs.getInt("pass_percent"));
                    user.setPassword(rs.getString("user_passcode"));
                    user.setAid(rs.getInt("aid"));
                    user.setUserAccountType(rs.getString("type"));
                    user.setDemoUser((rs.getInt("is_demo") != 0));

                    return user;
                }
            });

            CmCacheManager.getInstance().addToCache(CacheName.HA_USER, cacheKey, user);

            return user;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            Logger.getLogger(HaUser.class).error("*** Error looking up Catchup Math user: uid: " + uid, e);
            throw new HotMathException(e, "Error looking up Catchup Math user");
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }

    /**
     * Update this user record in db
     * 
     * (only select fields ...)
     * 
     * @throws Exception
     */
    public void updateUser(final HaUser user) throws Exception {
        final HaTestDef def = HaTestDefFactory.createTestDef(user.getAssignedTestName());

        int count = getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {

                String sql = "update HA_USER set active_test_id = ?, active_run_id = ?, active_segment = ?, active_run_session = ?, "
                        + "test_def_id = ? " + " where uid = ?";

                PreparedStatement pstat = con.prepareStatement(sql);
                pstat.setInt(1, user.getActiveTest());
                pstat.setInt(2, user.getActiveTestRunId());
                pstat.setInt(3, user.getActiveTestSegment());
                pstat.setInt(4, user.getActiveTestRunSession());
                pstat.setInt(5, def.getTestDefId());
                pstat.setInt(6, user.getUid());

                __logger.debug("Updating HA_USER: " + pstat.toString());

                return pstat;
            }
        });

        if (count == 0)
            throw new HotMathException("Could not update user record: " + user.getUid());

    }
    
    
    
    
    /** what is the first thing the user is shown?
     *  
     *  This is based on the user's current active state.
     *  
     *  If user's program is complete:
     *     If stopAfterComplete, then 
     *        set firstAction to END_OF_PROGRAM
     *     else 
     *        // this should not happen ..?
     *        // unless error/corrupted ..?
     *        
     *  Else
     *      if runId > 0
     *          set firstAction to PRESCRIPTION
     *      else if testId > 0)
     *          set firstAction to QUIZ
     *                
     *      else
     *          set firstAction to WELCOME    
     *          
     *     
     */
    CmDestination firstDestination = null;
    public CmDestination determineFirstDestination(final Connection conn, UserInfo userInfo, CmProgramFlow programFlow) throws Exception {
        CmDestination destination = new CmDestination();
        
        
        /** Special cases 
         * 
         */
        if(programFlow.getUserProgram().isCustom() 
                && programFlow.getActiveFlowAction(conn).getPlace() == CmPlace.END_OF_PROGRAM) {
            /** is a custom quiz, so we must check separately.
             * 
             */
            destination.setPlace(CmPlace.END_OF_PROGRAM);
        }
        else if(userInfo.getRunId() > 0 && hasUserCompletedTestRun(conn, userInfo.getRunId())) {
            
            /** did the user pass this segment, if not
             *  then they must repeat it .. so program 
             *  is not complete.
             */

            HaTestRun testRun = HaTestRunDao.getInstance().lookupTestRun(userInfo.getRunId());
            if(!testRun.isPassing()) {
                
                programFlow.getActiveInfo().setActiveRunId(0);
                programFlow.getActiveInfo().setActiveTestId(0);
                programFlow.saveActiveInfo(conn);
                
                /** repeat the segment
                 * 
                 */
                destination.setPlace(CmPlace.QUIZ);
            }
            else {
                if(hasUserCompletedProgram(conn, userInfo, programFlow)) {
                    destination.setPlace(CmPlace.END_OF_PROGRAM);
                }
                else {
                    //programFlow.moveToNextProgramSegment(conn);
                    
                    //destination.setPlace(CmPlace.QUIZ);
                }
            }
        }
        else if(userInfo.getRunId() > 0) {
            
            if(programFlow.getActiveInfo().getActiveRunSession() == 0) {
                destination.setPlace(CmPlace.WELCOME);    
            }
            else {
                destination.setPlace(CmPlace.PRESCRIPTION);
            }
        }
        else if(userInfo.getTestId() > 0) {
            destination.setPlace(CmPlace.QUIZ);
        }
        else {
            destination.setPlace(CmPlace.WELCOME);
        }
        
        return destination;
    }
    
    
    /** Has this user completed the current active program ?
     * 
     * 
     *  NOTE: segment is 1 based.
     *  
     * @param userInfo
     * @return
     * @throws Exception
     */
    private boolean hasUserCompletedProgram(final Connection conn, UserInfo userInfo, CmProgramFlow cmProgram) throws Exception {
        int totalSegments = userInfo.getProgramSegmentCount();
        int thisSegment = userInfo.getTestSegment();
        
        //assert(thisSegment > 0);
        
        /** program segments are 1 based
         * 
         */
        if(thisSegment <= totalSegments) {
            
            /** we know it is valid
             * 
             */
            if(thisSegment == totalSegments) {
                /** on last segment
                 * 
                 */
                
                /** has the user viewed each lesson in active prescription?
                 * 
                 */
                if(userInfo.getRunId() > 0) {
                    if(hasUserCompletedTestRun(conn, userInfo.getRunId())) {
                        return true;
                    }
                }
            }
        }
        else {
            return true;  // over the end, bug?
        }

        /** default is not completed 
         * 
         */
        return false;
    }

    /** This test run is fully completed
    */

    private boolean hasUserCompletedTestRun(final Connection conn, int runId) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_COUNT_UNCOMPLETED_TEST_RUN_LESSONS_PROGRAM");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, runId);
            ResultSet rs = ps.executeQuery();
            rs.first();
            int cntToView = rs.getInt(1);
            
            return (cntToView == 0);
        }
        finally {
            SqlUtilities.releaseResources(null,ps, null);
        }
    }
    
}