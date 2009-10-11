package hotmath.gwt.cm_tools.client.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class StudentReportCardModel implements StudentReportCardModelI {

	private Integer adminUid;
	private String currentProgramName;
	private String currentProgramStatus;
	private Date firstActivityDate;
	private String groupName;
	private String initialProgramName;
	private String initialProgramStatus;
	private Date lastActivityDate;
	private List<String> prescribedLessonList;
	// private List<StudentProgramReportModelI> programReportList;
	private Integer quizAvgPassPercent;
	private Integer quizCount;
	private Integer quizPassCount;
	private Date reportEndDate;
	private Date reportStartDate;
	private Map<String,Integer> resourceUsage;
	private String studentName;
	private Integer studentUid;
	
	
	public Integer getAdminUid() {
		return adminUid;
	}

	public String getLastProgramName() {
		return currentProgramName;
	}

	public String getLastProgramStatus() {
		return currentProgramStatus;
	}

	public Date getFirstActivityDate() {
		return firstActivityDate;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getInitialProgramName() {
		return initialProgramName;
	}

	public String getInitialProgramStatus() {
		return initialProgramStatus;
	}

	public Date getLastActivityDate() {
		return lastActivityDate;
	}

	public List<String> getPrescribedLessonList() {
		return prescribedLessonList;
	}

	public Integer getQuizAvgPassPercent() {
		return quizAvgPassPercent;
	}

	public Integer getQuizCount() {
		return quizCount;
	}

	public Integer getQuizPassCount() {
		return quizPassCount;
	}

	public List<StudentProgramReportModelI> getProgramReportList() throws Exception {
		throw new UnsupportedOperationException();
	}

	public Date getReportEndDate() {
		return reportEndDate;
	}

	public Date getReportStartDate() {
		return reportStartDate;
	}

	public Map<String, Integer> getResourceUsage() {
		return resourceUsage;
	}

	public String getStudentName() {
		return studentName;
	}

	public Integer getStudentUid() {
		return studentUid;
	}

	public void setAdminUid(Integer uid) {
        adminUid = uid;
	}

	public void setLastProgramName(String name) {
		currentProgramName = name;
	}

	public void setLastProgramStatus(String status) {
		currentProgramStatus = status;
	}

	public void setFirstActivityDate(Date date) {
		firstActivityDate = date;
	}

	public void setGroupName(String name) {
		groupName = name;
	}

	public void setInitialProgramName(String name) {
        initialProgramName = name;
	}

	public void setInitialProgramStatus(String status) {
		initialProgramStatus = status;
	}

	public void setLastActivityDate(Date date) {
		lastActivityDate = date;
	}

	public void setPrescribedLessonList(List<String> list) {
		prescribedLessonList = list;
	}

	public void setProgramReportList(List<StudentProgramReportModelI> list) throws Exception {
		throw new UnsupportedOperationException();
	}

	public void setQuizAvgPassPercent(Integer percent) {
		quizAvgPassPercent = percent;
	}

	public void setQuizCount(Integer count) {
		quizCount = count;
	}

	public void setQuizPassCount(Integer count) {
		quizPassCount = count;
	}

	public void setReportEndDate(Date date) {
		reportEndDate = date;
	}

	public void setReportStartDate(Date date) {
		reportStartDate = date;
	}

	public void setResourceUsage(Map<String, Integer> map) {
		resourceUsage = map;
	}

	public void setStudentName(String name) {
		studentName = name;
	}

	public void setStudentUid(Integer uid) {
		studentUid = uid;
	}

}