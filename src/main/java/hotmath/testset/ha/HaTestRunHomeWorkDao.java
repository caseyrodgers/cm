package hotmath.testset.ha;

import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * defines data access methods for HaTest
 * 
 * @author Bob
 * 
 */

public class HaTestRunHomeWorkDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(HaTestRunHomeWorkDao.class);

    static private HaTestRunHomeWorkDao __instance;

    static public HaTestRunHomeWorkDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (HaTestRunHomeWorkDao) SpringManager.getInstance().getBeanFactory().getBean(HaTestRunHomeWorkDao.class.getName());
        }
        return __instance;
    }

    private HaTestRunHomeWorkDao() {
        /** empty */
    }
   
    /** Save the value of a user input widget answer
     * 
     * @TODO: this does not seem to belong in this DAO. 
     * 
     * @param runId
     * @param pid
     * @param correct
     */
    public void saveUserTutorInputWidgetAnswer(final int runId, final String pid, final boolean correct) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into HA_TEST_RUN_WIDGET_INPUT_ANSWERS(run_id, pid, correct,answer_time)values(?,?,?,now())";                
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, runId);
                ps.setString(2, pid);
                ps.setInt(3,correct?1:0);
                
                return ps;
            }
        });
    }
    
}