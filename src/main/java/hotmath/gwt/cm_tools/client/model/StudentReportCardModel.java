package hotmath.gwt.cm_tools.client.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class StudentReportCardModel implements StudentReportCardModelI {

	private Integer adminUid;
	private String currentProgramName;
	private String currentProgramStatus;
	private Date firstLoginDate;
	private String initialProgramName;
	private String initialProgramStatus;
	private Date lastLoginDate;
	private List<StudentProgramReportModelI> programReportList;
	private Integer quizCount;
	private Integer quizPassCount;
	private Map<String,Integer> resourceUsage;
	private Integer studentUid;
	
	
	public Integer getAdminUid() {
		return adminUid;
	}

	public String getCurrentProgramName() {
		return currentProgramName;
	}

	public String getCurrentProgramStatus() {
		return currentProgramStatus;
	}

	public Date getFirstLoginDate() {
		return firstLoginDate;
	}

	public String getInitialProgramName() {
		return initialProgramName;
	}

	public String getInitialProgramStatus() {
		return initialProgramStatus;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public Integer getQuizCount() {
		return quizCount;
	}

	public Integer getQuizPassCount() {
		return quizPassCount;
	}

	public List<StudentProgramReportModelI> getProgramReportList() {
		return programReportList;
	}

	public Map<String, Integer> getResourceUsage() {
		return resourceUsage;
	}

	public Integer getStudentUid() {
		return studentUid;
	}

	public void setAdminUid(Integer uid) {
        adminUid = uid;
	}

	public void setCurrentProgramName(String name) {
		currentProgramName = name;
	}

	public void setCurrentProgramStatus(String status) {
		currentProgramStatus = status;
	}

	public void setFirstLoginDate(Date date) {
		firstLoginDate = date;
	}

	public void setInitialProgramName(String name) {
        initialProgramName = name;
	}

	public void setInitialProgramStatus(String status) {
		initialProgramStatus = status;
	}

	public void setLastLoginDate(Date date) {
		lastLoginDate = date;
	}

	public void setProgramReportList(List<StudentProgramReportModelI> list) {
		programReportList = list;
	}

	public void setQuizCount(Integer count) {
		quizCount = count;
	}

	public void setQuizPassCount(Integer count) {
		quizPassCount = count;
	}

	public void setResourceUsage(Map<String, Integer> map) {
		resourceUsage = map;
	}

	public void setStudentUid(Integer uid) {
		studentUid = uid;
	}

}