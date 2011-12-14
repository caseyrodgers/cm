package hotmath.gwt.cm_admin.server.model.activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author bob
 *
 */

public class StudentActivitySummaryModel {
	
	private String programName;
	private String programType;
	private String status;
	private String studentName;
	
	private int allQuizAvg;
	private int passedQuizAvg;
	private int sectionCount;
	private int sectionNum;
	private int totalQuizzes;
	
	List<Integer> quizScores = new ArrayList<Integer>();

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTotalQuizzes() {
		return totalQuizzes;
	}

	public void setTotalQuizzes(int totalQuizzes) {
		this.totalQuizzes = totalQuizzes;
	}

	public int getSectionCount() {
		return sectionCount;
	}

	public void setSectionCount(int totalSections) {
		this.sectionCount = totalSections;
	}

	public int getSectionNum() {
		return sectionNum;
	}

	public void setSectionNum(int sectionNum) {
		this.sectionNum = sectionNum;
	}

	public int getPassedQuizAvg() {
		return passedQuizAvg;
	}

	public void setPassedQuizAvg(int passedQuizAvg) {
		this.passedQuizAvg = passedQuizAvg;
	}

	public int getAllQuizAvg() {
		return allQuizAvg;
	}

	public void setAllQuizAvg(int allQuizAvg) {
		this.allQuizAvg = allQuizAvg;
	}

	public List<Integer> getQuizScores() {
		return quizScores;
	}

	public void setQuizScores(List<Integer> quizScores) {
		this.quizScores = quizScores;
	}
	

}
