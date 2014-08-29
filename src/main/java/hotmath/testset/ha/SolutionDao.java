package hotmath.testset.ha;

import hotmath.ProblemID;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.CompressHelper;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.SolutionContext;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.CmExceptionGlobalContextNotFound;
import hotmath.spring.SpringManager;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jdom.input.SAXBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Provide DAO functionality for HaTestDefs
 * 
 * @author casey
 * 
 */
public class SolutionDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(HaTestDefDao.class);

    static private SolutionDao __instance;

    static public SolutionDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (SolutionDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(SolutionDao.class.getName());
        }
        return __instance;
    }

    private SolutionDao() {
        /** Empty */
    }

    /**
     * Return all contexts associated with this PID (problem set).
     * 
     * The will be from 1-N (zero based)
     * 
     * @param runId
     * @param pid
     * @return
     */
    public SolutionContext getSolutionContext(int runId, final String pid) throws CmException {

        if (runId == 0) {
            return getGlobalSolutionContext(pid);
        }

        String sql = "select problem_number, variables from HA_SOLUTION_CONTEXT where run_id = ? and pid = ? order by id";
        List<SolutionContext> matches = getJdbcTemplate().query(sql,
                new Object[] { runId, pid },
                new RowMapper<SolutionContext>() {
                    @Override
                    public SolutionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String solnCtx;
                        try {
                            solnCtx = loadSolutionContextString(rs);
                        }
                        catch (Exception e) {
                            throw new SQLException(e);
                        }
                        return new SolutionContext(pid, rs.getInt("problem_number"), solnCtx);
                    }
                });
        return matches.size() > 0 ? matches.get(0) : null;
    }
    
    
    public SolutionContext getSolutionContext_Debug(int runId, final String pid) throws CmException {

        if (runId == 0) {
            return getGlobalSolutionContext(pid);
        }

        String sql = "select problem_number, variables from junk where run_id = ? and pid = ? order by id";
        List<SolutionContext> matches = getJdbcTemplate().query(sql,
                new Object[] { runId, pid },
                new RowMapper<SolutionContext>() {
                    @Override
                    public SolutionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String solnCtx;
                        try {
                            solnCtx = loadSolutionContextString(rs);
                        }
                        catch (Exception e) {
                            throw new SQLException(e);
                        }
                        return new SolutionContext(pid, rs.getInt("problem_number"), solnCtx);
                    }
                });
        return matches.size() > 0 ? matches.get(0) : null;
    }

   
    public String getSolutionContextString(int runId, String pid) {
        final ProblemID pidO = new ProblemID(pid);
        final String sql = "select variables from HA_SOLUTION_CONTEXT where run_id = ? and pid = ? and problem_number = ?";
        List<String> matches = getJdbcTemplate().query(sql,
                new Object[] { runId, pid, pidO.getProblemSetProblemNumber() },
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        __logger.debug("problem_number: " + pidO.getProblemNumber());
                        try {
                            return loadSolutionContextString(rs);
                        }
                        catch (Exception e) {
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return matches.size() > 0 ? matches.get(0) : null;
    }

    public List<LessonModel> getLessonsForPID(String pid) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_LESSON_FOR_PID");
        List<LessonModel> list = null;
        try {
            list = getJdbcTemplate().query(sql, new Object[] { pid }, new RowMapper<LessonModel>() {
                @Override
                public LessonModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    LessonModel data = new LessonModel(rs.getString("lesson"), rs.getString("lesson_file"));
                    return data;
                }
            });
        } catch (DataAccessException e) {
            __logger.error(String.format("getLessonsForPID(): lessonFile: %s", pid), e);
            throw e;
        }
        if (list == null)
            list = new ArrayList<LessonModel>();
        return list;
    }

    private String loadSolutionContextString(ResultSet rs) throws Exception {
        byte[] compressed = rs.getBytes("variables");
        if (compressed[0] != "{".getBytes("UTF-8")[0]) {
            return CompressHelper.decompress(compressed);
        }
        else {
            return rs.getString("variables");
        }
    }
    
   
    public String convertSolutionContext(byte[] variables) throws Exception {
        if (variables[0] != "{".getBytes("UTF-8")[0]) {
            return CompressHelper.decompress(variables);
        }
        else {
            return new String(variables);
        }
    }
    
    public void removeSolutionContext(final int runId, final String pid) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = null;
                String sql = "delete from HA_SOLUTION_CONTEXT where run_id = ? & and pid = ?";
                ps = con.prepareStatement(sql);
                ps.setInt(1, runId);
                ps.setString(2, pid);
                return ps;
            }
        });
    }
    

    /**
     * TODO: Should we allow overwriting of existing Solution Context?
     * 
     * @param runId
     * @param pid
     * @param problemNumber
     * @param contextJson
     */
    public void saveSolutionContext(final int runId, final String pid, final int problemNumber, final String contextJson) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = null;
                String sql = "insert into HA_SOLUTION_CONTEXT(time_viewed, run_id, pid, problem_number, variables)values(?,?,?,?,?)";
                ps = con.prepareStatement(sql);

                ps.setDate(1, new Date(System.currentTimeMillis()));
                ps.setInt(2, runId);
                ps.setString(3, pid);
                ps.setInt(4, problemNumber);

                byte[] inBytes = null;
                try {
                    inBytes = contextJson.getBytes("UTF-8");

                    byte[] outBytes = CompressHelper.compress(inBytes);
                    ps.setBytes(5, outBytes);

                    if (__logger.isDebugEnabled())
                        __logger.debug("in len: " + inBytes.length + ", out len: " + outBytes.length);
                } catch (UnsupportedEncodingException e) {
                    __logger.error(String.format("*** Error saving solution context for run_id: %d, pid: %s, prob#: %d",
                            runId, pid, problemNumber), e);
                    throw new SQLException(e.getLocalizedMessage());
                }
                return ps;
            }
        });
    }

    public void saveSolutionContextCompressed(final int runId, final String pid, final int problemNumber, final String contextJson) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = null;
                String sql = "insert into HA_SOLUTION_CONTEXT_COMPRESSED(time_viewed, run_id, pid, problem_number, variables)values(?,?,?,?,?)";
                ps = con.prepareStatement(sql);

                ps.setDate(1, new Date(System.currentTimeMillis()));
                ps.setInt(2, runId);
                ps.setString(3, pid);
                ps.setInt(4, problemNumber);

                byte[] inBytes = null;
                try {
                    inBytes = contextJson.getBytes("UTF-8");

                    byte[] outBytes = CompressHelper.compress(inBytes);
                    ps.setBytes(5, outBytes);

                    if (__logger.isDebugEnabled())
                        __logger.debug("in len: " + inBytes.length + ", out len: " + outBytes.length);
                } catch (UnsupportedEncodingException e) {
                    __logger.error(String.format("*** Error saving solution context for run_id: %d, pid: %s, prob#: %d",
                            runId, pid, problemNumber), e);
                    throw new SQLException(e.getLocalizedMessage());
                }
                return ps;
            }
        });
    }

    public String getSolutionContextCompressedString(int runId, String pid) {
        ProblemID pidO = new ProblemID(pid);
        // TODO: remove following hard coding
        pidO.setProblemSetProblemNumber(1);
        String sql = "select variables from HA_SOLUTION_CONTEXT_COMPRESSED where run_id = ? and pid = ? and problem_number = ? limit 1";
        List<String> matches = getJdbcTemplate().query(sql,
                new Object[] { runId, pid, pidO.getProblemSetProblemNumber() },
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        byte[] compressed = rs.getBytes("variables");
                        try {
                            return CompressHelper.decompress(compressed);
                        }
                        catch (Exception e) {
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return matches.size() > 0 ? matches.get(0) : null;
    }

    public String getSolutionXML(String pid) {
        String sql = "select solutionxml from SOLUTIONS where problemindex = ?";
        return getJdbcTemplate().queryForObject(sql, new Object[] { pid },
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("solutionxml");
                    }
                });
    }

    /**
     * Return global context or null
     * 
     * @param pid
     * @param problemNumber
     * @return
     */
    public SolutionContext getGlobalSolutionContext(final String pid) throws CmException {

        String pidParts[] = pid.split("\\$");
        String pidBase = pidParts[0];
        String pidContextGuid = null;
        if (pidParts.length > 1) {
            pidContextGuid = pidParts[1];
        }
        else {
            return null;
        }

        if (pidContextGuid.length() < 10) {
            pidContextGuid = getGlobalSolutionContextNewest(pidBase, pidContextGuid);
        }

        if (pidContextGuid == null) {
            throw new CmExceptionGlobalContextNotFound(pid);
        }

        String sql = "select * from HA_SOLUTION_GLOBAL_CONTEXT where pid = ? and context_guid = ?";
        List<SolutionContext> contexts = getJdbcTemplate().query(sql, new Object[] { pidBase, pidContextGuid },
                new RowMapper<SolutionContext>() {
                    @Override
                    public SolutionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String pidFull = rs.getString("pid") + "$" + rs.getString("context_guid");
                        SolutionContext c = new SolutionContext(pidFull, rs.getInt("problem_number"), rs.getString("variables"));
                        return c;
                    }
                });

        if (contexts.size() > 0) {
            return contexts.get(0);
        }
        else {
            throw new CmExceptionGlobalContextNotFound(pid);
        }
    }

    /**
     * Return the newest global context for this solution.
     * 
     * This is the last one the authors created.
     * 
     * @param pid
     * @param problemNumber
     * @return
     * @throws CmExceptionGlobalContextNotFound
     */
    public String getGlobalSolutionContextNewest(String pid, String problemNumber) throws CmExceptionGlobalContextNotFound {
        String sql = "select * from HA_SOLUTION_GLOBAL_CONTEXT where pid = ? and problem_number = ? ORDER BY create_time desc limit 1";
        List<String> contexts = getJdbcTemplate().query(sql, new Object[] { pid, problemNumber },
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("context_guid");
                    }
                });

        if (contexts.size() == 0) {
            throw new CmExceptionGlobalContextNotFound(pid + "$" + problemNumber);
        }
        else {
            return contexts.get(0);
        }
    }

    /**
     * Save all global solution contexts for a given pid.
     * 
     * @param pid
     * @param contexts
     */
    public void saveGlobalSolutionContexts(final String pid, final CmList<String> contexts) {
        __logger.debug("saving global solution contexts for: " + pid);

        // getJdbcTemplate().execute("delete from HA_SOLUTION_GLOBAL_CONTEXT where pid = '"
        // + pid + "'");
        String sql = "INSERT INTO HA_SOLUTION_GLOBAL_CONTEXT(create_time, pid, context_guid, problem_number, variables)values(now(),?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String context = contexts.get(i);
                ps.setString(1, pid);

                String contextGuid = UUID.randomUUID().toString();

                ps.setString(2, contextGuid);
                ps.setInt(3, (i + 1));
                ps.setString(4, context);
            }

            @Override
            public int getBatchSize() {
                return contexts.size();
            }
        });

        /**
         * Update SOLUTION_DYNAMIC table to track when contexts are created
         * 
         */
        int updated = getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = null;
                String sql = "update SOLUTION_DYNAMIC set global_context_created = now() where pid = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, pid);
                return ps;
            }
        });
        if (updated == 0) {
            __logger.warn("No SOLUTION_DYNAMIC record found for pid: " + pid);
        }

        __logger.debug("contexts saved");
    }

    /**
     * Get all solution pids that are dynamic
     * 
     * @param pid
     * @return
     */
    public List<String> getDynamicSolutionPidsNotProcessed() throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GLOBAL_CONTEXT_PIDS_TO_PROCESS");
        List<String> pids = getJdbcTemplate().query(sql, new Object[] {},
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("pid");
                    }
                });
        return pids;
    }

    /**
     * Return all global contexts defined for this pid
     * 
     * @param pid
     * @return
     */
    public List<SolutionContext> getGlobalSolutionContextAll(String pid) {
        String sql = "select * from HA_SOLUTION_GLOBAL_CONTEXT where pid = ? order by problem_number";
        List<SolutionContext> pids = getJdbcTemplate().query(sql, new Object[] { pid },
                new RowMapper<SolutionContext>() {
                    @Override
                    public SolutionContext mapRow(ResultSet rs, int rowNum) throws SQLException {
                        String fullPid = rs.getString("pid") + "$" + rs.getString("context_guid");
                        return new SolutionContext(fullPid, rs.getInt("problem_number"), rs.getString("variables"));
                    }
                });
        return pids;
    }

    public void deleteSolution(final String pid) throws Exception {

        int deleted = getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = null;
                String sql = "delete from SOLUTIONS where problemindex = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, pid);
                return ps;
            }
        });

        if (deleted == 0) {
            throw new CmException("Solution not found: " + pid);
        }
    }
    
    
    
    /**
     * Given the a problem PID, determine the type of problem
     * 
     * Meaning what type of input is required
     * 
     * @param defaultLabel
     * @return
     */
    public ProblemType getProblemType(String pid) throws Exception {
        String sql = "select problemindex, solutionxml from SOLUTIONS where problemindex = ?";
        return getJdbcTemplate().queryForObject(sql, new Object[] {pid}, new RowMapper<ProblemType>() {
            @Override
            public ProblemType mapRow(ResultSet rs, int rowNum) throws SQLException {
                String xml = rs.getString("solutionxml");
                return determineProblemType(xml);
            }
        });
    }
    
    
    /** Look at the associated XML and determine the input type
     * 
     * @param htmlOrXml
     * @return
     */
    static public ProblemType determineProblemType(String htmlOrXml) {
        try {

            /**
             * might be XML (hmsl ) or HTML (rendered)
             * 
             */
            String psHtml = "";
            if (htmlOrXml.indexOf("<hmsl") > -1) {
                /** is XmL */
                Document doc = parseSolutionXml(htmlOrXml);
                Element docEle = doc.getDocumentElement();
                NodeList elements = docEle.getElementsByTagName("statement");
                if (elements.getLength() > 0) {
                    psHtml = elements.item(0).getTextContent();
                }
            } else {
                /** is HTML */
                /** extract just the problem statement */
                int su = htmlOrXml.indexOf("stepunit"); // first step
                if (su >= 1) {
                    psHtml = htmlOrXml.substring(0, su);
                }
            }

            
            if(psHtml.indexOf("hm_flash_widget") > -1  && _isWhiteboardWidget(psHtml)) {
            	return ProblemType.WHITEBOARD;
            }
            else if ((psHtml.indexOf("hm_flash_widget") > -1 || psHtml.indexOf("hotmath:flash") > -1) && psHtml.indexOf("not_used") == -1) {
                return ProblemType.INPUT_WIDGET;
            } else if (psHtml.indexOf("<div widget='") > -1) {
                return ProblemType.INPUT_WIDGET;
            } else if (psHtml.indexOf("hm_question_def") > -1) {
                return ProblemType.MULTI_CHOICE;
            } else {
                return ProblemType.WHITEBOARD;
            }

        } catch (Exception e) {
            __logger.error("Error determining problem type: " + htmlOrXml, e);
        }

        return ProblemType.UNKNOWN;
    }

	private static boolean _isWhiteboardWidget(String psHtml) {
		int startIndex = psHtml.indexOf("hm_flash_widget_def");
		if(startIndex==-1) {
			return true;
		}
		
		int endIndex = psHtml.indexOf("</div>", startIndex);
		String widgetDef = psHtml.substring(startIndex, endIndex);
		
		return widgetDef.indexOf("whiteboard") > -1;
		
	}

	private static Document parseSolutionXml(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }    
}
