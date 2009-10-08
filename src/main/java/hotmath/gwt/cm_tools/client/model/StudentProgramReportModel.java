package hotmath.gwt.cm_tools.client.model;

import java.util.Date;
import java.util.Map;

public class StudentProgramReportModel implements StudentProgramReportModelI {

	private Date assignedDate;
	private Date firstLoginDate;
	private Date lastLoginDate;
	private String programName;
	private String programStatus;
	private Integer quizCount;
	private Integer quizPassCount;
	private Map<String, Integer> resourceUsage;
	private Integer studentUid;
	
	public Date getAssignedDate() {
		return assignedDate;
	}

	public Date getFirstLoginDate() {
		return firstLoginDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public String getProgramDescr() {
		return programName;
	}

	public String getProgramStatus() {
		return programStatus;
	}

	public Integer getQuizCount() {
		return quizCount;
	}

	public Integer getQuizPassCount() {
		return quizPassCount;
	}

	public Map<String, Integer> getResourceUsage() {
		return resourceUsage;
	}

	public Integer getStudentUid() {
		return studentUid;
	}

	public void setAssignedDate(Date date) {
		assignedDate = date;
	}

	public void setFirstLoginDate(Date date) {
        firstLoginDate = date;
	}

	public void setLastLoginDate(Date date) {
		lastLoginDate = date;
	}

	public void setProgramDescr(String name) {
		programName = name;
   	}

	public void setProgramStatus(String status) {
		programStatus = status;
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
