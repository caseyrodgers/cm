package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModel;
import hotmath.gwt.cm_tools.client.model.StudentReportCardModelI;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Defines data access methods for a Catchup Math Report Card
 * 
 * @author bob
 * 
 */

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
			 StudentModelI sm = stuDao.getStudentModel(conn, studentUid, false);
			 rval.setAdminUid(sm.getAdminUid());
			 rval.setGroupName(sm.getGroup());

			 CmUserProgramDao upDao = new CmUserProgramDao();
			 List<StudentUserProgramModel> list = upDao.loadProgramInfoAll(conn, studentUid);
			 List<StudentUserProgramModel> filteredList = findFirstLastUserProgramInDateRange(list, beginDate, endDate);

			 if (filteredList.size() < 1) return rval;

			 StudentUserProgramModel pm = filteredList.get(0);
			 String testName = pm.getTestName();
			 List<String> chapList = pm.getConfig().getChapters();
			 if (chapList != null) {
				 // getChapters() returns a List - using only the first one
				 String chapter = chapList.get(0).trim();
				 String progLongName = buildProgramName(conn, pm, chapter, testName);
    			 rval.setInitialProgramName(progLongName);
			 }
			 else {
    			 rval.setInitialProgramName(testName);
			 }
			 rval.setInitialProgramDate(pm.getCreateDate());
			 
			 pm = filteredList.get(filteredList.size() - 1);
			 testName = pm.getTestName();
			 chapList = pm.getConfig().getChapters();
			 if (chapList != null) {
				 // getChapters() returns a List - using only the first one
				 String chapter = chapList.get(0).trim();
				 String progLongName = buildProgramName(conn, pm, chapter, testName);
    			 rval.setLastProgramName(progLongName);
			 }
			 else {
			     rval.setLastProgramName(testName);
			 }
			 rval.setLastProgramDate(pm.getCreateDate());

			 // load HaTest and HaTestRun
			 List<HaTest> testList = loadQuizData(conn, filteredList);
			 setFirstLastProgramStatus(rval, testList);

			 // set first and last activity date based on quiz data (HaTest and HaTestRun)
			 setFirstLastActivityDate(rval, testList);
			 
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

	 private String buildProgramName(final Connection conn, StudentUserProgramModel pm, String chapter, String testName) throws Exception {

		 CmAdminDao adminDao = new CmAdminDao();
		 String subjectId = pm.getTestDef().getSubjectId();
		 String progId = pm.getTestDef().getProgId();
		 
		 CmList<ChapterModel> cmList = adminDao.getChaptersForProgramSubject(conn, progId, subjectId);
		 String chapNumb = null;
		 for (ChapterModel cm : cmList) {
		     if (chapter.equalsIgnoreCase(cm.getTitle().trim())) {
                 chapNumb = cm.getNumber().trim();
			 }
		 }
		 return (chapNumb != null) ?
				 String.format("%s, [%s] %s", testName, chapNumb, chapter) :
                 String.format("%s, %s", testName, chapter);
	 }

	 private void setFirstLastProgramStatus(StudentReportCardModelI rval,
			List<HaTest> testList) {
		 // no HA_TEST data found... (should this be possible, data problem?)
		 if (testList == null || testList.size() < 1) {
			 rval.setInitialProgramStatus("Section 1");
			 rval.setLastProgramStatus("Section 1");
			 return;
		 }
		 int section = testList.get(0).getSegment();
		 int totalSections = testList.get(0).getTotalSegments();
		 StringBuilder sb = new StringBuilder();
		 if (section < totalSections) {
		     String initialStatus = sb.append("Section ").append(section).append(" of ").append(totalSections).toString();
		     rval.setInitialProgramStatus(initialStatus);
		 }
		 else {
			 rval.setInitialProgramStatus("Completed");
		 }
		 
		 section = testList.get(testList.size()-1).getSegment();
		 totalSections =  testList.get(testList.size()-1).getTotalSegments();
		 sb.delete(0, sb.length());
		 if (section < totalSections) {
		     String status = sb.append("Section ").append(section).append(" of ").append(totalSections).toString();
		     rval.setLastProgramStatus(status);
		 }
		 else {
			 rval.setLastProgramStatus("Completed");
		 }
	}

	 private List<StudentUserProgramModel> findFirstLastUserProgramInDateRange(List<StudentUserProgramModel> list, Date beginDate, Date endDate) {

		 List<StudentUserProgramModel> l = new ArrayList<StudentUserProgramModel>();

		 //TODO: apply date range if requested
		 l.addAll(list);

		 return l;
	 }

	 private void setFirstLastActivityDate(StudentReportCardModelI rc, List<HaTest>testList) {
		 if (testList != null && testList.size() > 0) {
			 Date date = testList.get(0).getCreateTime();
			 rc.setFirstActivityDate(date);

			 date = testList.get(testList.size() - 1).getCreateTime();
		  	 rc.setLastActivityDate(date);
		 }
	  	 
	  	 //TODO: refine "last" activity date using HA_TEST_RUN.run_time
	 }

	 /** Load the quiz information, throws Exception if no quizzes loaded
	  * 
	  * @param conn
	  * @param list
	  * @return
	  * @throws Exception
	  */
	 private List<HaTest> loadQuizData(final Connection conn, List<StudentUserProgramModel> list) throws Exception {
		 return HaTestDao.loadTestsForProgramList(conn, list);
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
			 // login count
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
				 usageMap.put("login", loginCount);
			 }
			 SqlUtilities.releaseResources(rs, ps, null);

			 // resource usage counts
			 sql = CmMultiLinePropertyReader.getInstance().getProperty("RESOURCE_USAGE_COUNT");
			 String progIds = getProgIdList(list);
			 ps = conn.prepareStatement(sql.replaceAll("XXX", progIds));
			 rs = ps.executeQuery();
			 while (rs.next()) {
				 Integer count = rs.getInt(1);
				 String resource = rs.getString(2);
				 usageMap.put(resource, count);
			 }

		 }
		 catch (Exception e) {
			 logger.error(String.format("*** Error obtaining usage results for student UID: %d", uid), e);
			 throw new Exception(String.format("*** Error obtaining usage results for student with UID: %d", uid));	        	
		 }
		 finally {
			 SqlUtilities.releaseResources(rs, ps, null);
		 }


	 }

	 private void loadPrescribedLessons(List<StudentUserProgramModel> list, StudentReportCardModelI rc, Connection conn) throws Exception {
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 Integer uid = list.get(0).getUserId();

		 try { 
			 String sql = CmMultiLinePropertyReader.getInstance().getProperty("LESSONS_ASSIGNED");
			 String progIds = getProgIdList(list);
			 ps = conn.prepareStatement(sql.replaceFirst("XXX", progIds));
			 rs = ps.executeQuery();
			 List<String> lessons = new ArrayList<String>();
			 while (rs.next()) {
				 String lesson = rs.getString(1);
				 lessons.add(lesson);
			 }
			 rc.setPrescribedLessonList(lessons);
		 }
		 catch (Exception e) {
			 logger.error(String.format("*** Error obtaining prescribed lessons for student UID: %d", uid), e);
			 throw new Exception(String.format("*** Error obtaining prescriobed lessons for student with UID: %d", uid));	        	
		 }
		 finally {
			 SqlUtilities.releaseResources(rs, ps, null);
		 }
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
