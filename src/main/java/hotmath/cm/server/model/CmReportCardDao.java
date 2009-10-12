package hotmath.cm.server.model;

import hotmath.assessment.InmhItemData;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

	public class CmReportCardDao {

	    private static final Logger logger = Logger.getLogger(CmReportCardDao.class);
	    
	    public CmReportCardDao() {
	    }

	    /**
	     */
	    public StudentReportCardModelI getStudentReportCard(Integer studentUid, Date beginDate, Date endDate) throws Exception {
	        
	        Connection conn = null;
	        StudentReportCardModelI rval = new StudentReportCardModel();
	        
	        try {
	            conn = HMConnectionPool.getConnection();
	            
	            // load student info
	            CmStudentDao stuDao = new CmStudentDao();
	            StudentModel sm = stuDao.getStudentModel(conn, studentUid, false);
	            rval.setAdminUid(sm.getAdminUid());
	            rval.setGroupName(sm.getGroup());
	            
		        CmUserProgramDao upDao = new CmUserProgramDao();
		        List<StudentUserProgramModel> list = upDao.loadProgramInfoAll(conn, studentUid);
                List<StudentUserProgramModel> filteredList = findFirstLastUserProgramInDateRange(list, beginDate, endDate);
                
                if (filteredList.size() < 1) return rval;
                
                rval.setInitialProgramName(filteredList.get(0).getTestName());
                rval.setInitialProgramDate(filteredList.get(0).getCreateDate());
                if (filteredList.size() > 1) {
                    rval.setLastProgramName(filteredList.get(filteredList.size() - 1).getTestName());
                    rval.setLastProgramDate(filteredList.get(filteredList.size() - 1).getCreateDate());
                }
                
                // load quiz data for initial through last programs
                loadQuizResults(filteredList, rval, conn);
                
                // load resource usage data for initial through last programs
                loadResourceUsage(filteredList, rval, conn);
                
                // load prescribed lesson data for initial through last programs
                loadPrescribedLessons(filteredList, rval, conn);
                
	        } catch (Exception e) {
	            logger.error(String.format("*** Error getting report card for Student uid: %d", studentUid), e);
	            throw new Exception("*** Error getting student report card data ***");
	        } finally {
	            SqlUtilities.releaseResources(null, null, conn);
	        }
	        return rval;
	    }
	    
	    private List<StudentUserProgramModel> findFirstLastUserProgramInDateRange(List<StudentUserProgramModel> list, Date beginDate, Date endDate) {
	    	
	    	List<StudentUserProgramModel> l = new ArrayList<StudentUserProgramModel>();

	    	//TODO: apply date range
	    	l.addAll(list);
	    	
	    	return l;
	    }
	    
	    private void loadQuizResults(List<StudentUserProgramModel> list, StudentReportCardModelI rc, Connection conn) throws Exception {
	        PreparedStatement ps = null;
	        ResultSet rs = null;
	        String sql;
	        
	        String progIds = getProgIdList(list);
	        try {
	        	sql = CmMultiLinePropertyReader.getInstance().getProperty("PROGRAM_QUIZ_COUNT");
	            ps = conn.prepareStatement(sql.replaceFirst("XXX", progIds));
	            rs = ps.executeQuery();
	            if (rs.next()) {
	                Integer quizCount = rs.getInt(1);
	                rc.setQuizCount(quizCount);
	            }
	            SqlUtilities.releaseResources(rs, ps, null);
	            
	        	sql = CmMultiLinePropertyReader.getInstance().getProperty("PROGRAM_PASSED_QUIZ_COUNT");
	            ps = conn.prepareStatement(sql.replaceFirst("XXX", progIds));
                rs = ps.executeQuery();
	            if (rs.next()) {
	                Integer quizCount = rs.getInt(1);
	                rc.setQuizPassCount(quizCount);
	            }
	            SqlUtilities.releaseResources(rs, ps, null);
	            
	        	sql = CmMultiLinePropertyReader.getInstance().getProperty("PROGRAM_AGGREGATE_QUIZ_RESULTS");
	            ps = conn.prepareStatement(sql.replaceFirst("XXX", progIds));
	            rs = ps.executeQuery();
	            if (rs.next()) {
	                Integer answeredCorrect = rs.getInt("answered_correct");
	                Integer answeredIncorrect = rs.getInt("answered_incorrect");
	                Integer notAnswered = rs.getInt("not_answered");
	                Integer total = answeredCorrect + answeredIncorrect + notAnswered;
	                Integer avgPassPercent = 0;
	                if (total > 0) {
	            	    avgPassPercent = (100 * answeredCorrect) / total;
		                rc.setQuizAvgPassPercent(avgPassPercent);
	                }
	            }
	        }
            catch (Exception e) {
            	Integer uid = list.get(0).getUserId();
                logger.error(String.format("*** Error obtaining quiz results for student UID: %d", uid), e);
                throw new Exception(String.format("*** Error obtaining quiz results for student with UID: %d", uid));
            }
	        finally {
	        	SqlUtilities.releaseResources(rs, ps, null);
	        }

	    }

	    private void loadResourceUsage(List<StudentUserProgramModel> list, StudentReportCardModelI rc, Connection conn) throws Exception {
	        PreparedStatement ps = null;
	        ResultSet rs = null;
        	Integer uid = list.get(0).getUserId();
	        
	        try { 
    	    	// first login count
	        	String date = String.format("%1$tY-%1$tm-%1$td", list.get(0).getCreateDate());
	        	String sql = CmMultiLinePropertyReader.getInstance().getProperty("LOGIN_COUNT");
	            ps = conn.prepareStatement(sql);
	            ps.setInt(1, uid);
	            ps.setString(2, date);
	            rs = ps.executeQuery();
	            Map<String, Integer> usageMap = new HashMap<String, Integer>();
	            rc.setResourceUsage(usageMap);
	            if (rs.first()) {
	            	Integer loginCount = rs.getInt(1);
	            	usageMap.put("Login", loginCount);
	            }
	            SqlUtilities.releaseResources(rs, ps, null);
	            
	            sql = CmMultiLinePropertyReader.getInstance().getProperty("RESOURCE_USAGE_COUNT");
		        String progIds = getProgIdList(list);
		        ps = conn.prepareStatement(sql.replaceFirst("XXX", progIds));
		        rs = ps.executeQuery();
		        while (rs.next()) {
		        	Integer count = rs.getInt(1);
		        	String resource = rs.getString(2);
		        	usageMap.put(resource, count);
		        }
	            
	        }
	        catch (Exception e) {
                logger.error(String.format("*** Error obtaining quiz results for student UID: %d", uid), e);
                throw new Exception(String.format("*** Error obtaining quiz results for student with UID: %d", uid));	        	
	        }
	        finally {
	        	SqlUtilities.releaseResources(rs, ps, null);
	        }
	    	
	    	
	    }

	    private void loadPrescribedLessons(List<StudentUserProgramModel> list, StudentReportCardModelI rc, Connection conn) {
	    	
	    }

		private String getProgIdList(List<StudentUserProgramModel> list) {
			StringBuilder sb = new StringBuilder();
	        boolean first = true;
	        for (StudentUserProgramModel m : list) {
	        	if (first)
	        	    first = false;
	        	else
	        		sb.append(",");
	        	sb.append(m.getId());
	        }
			return sb.toString();
		}

		private StudentReportCardModelI loadReportCard(ResultSet rs) throws Exception {
	    	
	    	if (!rs.next()) {
	    		throw new Exception("No data found");
	    	}
	    	StudentReportCardModelI rval = new StudentReportCardModel();
	    	
	    	rval.setStudentUid(rs.getInt(1));
	    	rval.setAdminUid(rs.getInt(2));
	        String prog = rs.getString(3);
	        String initial = rs.getString(4);
	        String status  = rs.getString(5);
	    	rval.setInitialProgramName(prog + initial);
	    	rval.setLastProgramName(prog + status);
	    	
	    	java.sql.Date d = rs.getDate(6);
	    	Date createDate = new Date(d.getTime());
	    	String passPercent = rs.getString(7);
	    	java.sql.Date firstLogin = rs.getDate(7);
	    	rval.setFirstActivityDate(firstLogin);
	    	
	    	rval.setQuizCount(rs.getInt(8));
	    	rval.setQuizPassCount(rs.getInt(9));
	    	
	    	return rval;
	    }

}
