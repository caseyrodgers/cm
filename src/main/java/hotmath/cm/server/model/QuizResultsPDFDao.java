package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * <codeQuizResultsPDFDao</code> supports Quiz Results PDFs
 * 
 * @author bob
 *
 */

public class QuizResultsPDFDao extends SimpleJdbcDaoSupport {

	private static final Logger LOGGER = Logger.getLogger(QuizResultsPDFDao.class);

    static private QuizResultsPDFDao __instance;

    static public QuizResultsPDFDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (QuizResultsPDFDao)SpringManager.getInstance().getBeanFactory().getBean("quizResultsPDFDao");
        }
        return __instance;
    }

    public void create(final QuizResultsModel model) throws Exception {

    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("INSERT_QUIZ_RESULTS_PDF");
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
    				PreparedStatement ps = connection.prepareStatement(sql);
    				ps.setInt(1, model.getRunId());
    				ps.setBytes(2, model.getQuizPDFbytes());
    				return ps;
    			}
    		});
    	} catch (Exception e) {
    		LOGGER.error("Error saving: " + model.toString(), e);
    		throw new Exception("Error saving HA_TEST_RUN_RESULTS_PDF", e);
    	}

    }
    

    /** return either the QuizResultsModel or null
     *  if QuizResulsModel does not exist for runId
     *  
     * @param runId
     * @return
     * @throws Exception
     */
    public QuizResultsModel read(int runId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("SELECT_QUIZ_RESULTS_PDF_BY_RUN_ID");

    	List<QuizResultsModel> models;
    	QuizResultsModel model = null;
    	try {
    		models = this.getJdbcTemplate().query(
    				sql,
    				new Object[]{runId},
    				new RowMapper<QuizResultsModel>() {
    					public QuizResultsModel mapRow(ResultSet rs, int rowNum) throws SQLException {
    						QuizResultsModel model = new QuizResultsModel();

    						model.setRunId(rs.getInt("run_id"));
    						model.setQuizPDFbytes(rs.getBytes("pdf"));

    						return model;
    					}
    				});
    		return models.size() == 0?null:models.get(0);
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error reading Quiz Results PDF, runId: %d", runId), e);
    		throw new Exception(e.getMessage(), e);
    	}
    }

}