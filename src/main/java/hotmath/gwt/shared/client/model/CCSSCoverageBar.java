package hotmath.gwt.shared.client.model;

import java.util.Date;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/**
 * 
 * @author bob
 *
 */

public class CCSSCoverageBar implements Response {
	private static final long serialVersionUID = -1510650977806015755L;

	String label;
	int assignmentCount;
	int lessonCount;
	int quizCount;
	int uniqueCount;
	int numberOfDays;
	Date beginDate;
	Date endDate;
	CmList<String> assignmentStdNames = new CmArrayList<String>();
	CmList<String> lessonStdNames = new CmArrayList<String>();
	CmList<String> quizStdNames = new CmArrayList<String>();
	CmList<String> uniqueStdNames = new CmArrayList<String>();

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getCount() {
		return assignmentCount + lessonCount + quizCount;
	}

	public int getAssignmentCount() {
		return assignmentCount;
	}

	public void setAssignmentCount(int assignmentCount) {
		this.assignmentCount = assignmentCount;
	}

	public int getAssignments() {
		return assignmentCount;
	}

	public int getLessonCount() {
		return lessonCount;
	}

	public void setLessonCount(int lessonCount) {
		this.lessonCount = lessonCount;
	}

	public int getLessons() {
		return lessonCount;
	}

	public int getQuizCount() {
		return quizCount;
	}

	public void setQuizCount(int quizCount) {
		this.quizCount = quizCount;
	}

	public int getUniqueCount() {
		return uniqueCount;
	}

	public void setUniqueCount(int uniqueCount) {
		this.uniqueCount = uniqueCount;
	}

	public int getStandards() {
		return uniqueCount;
	}

	public int getNumberOfDays() {
		return numberOfDays;
	}

	public void setNumberOfDays(int numberOfDays) {
		this.numberOfDays = numberOfDays;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getQuizzes() {
		return quizCount;
	}

	public CmList<String> getAssignmentStdNames() {
		return assignmentStdNames;
	}

	public void setAssignmentStdNames(CmList<String> assignmentStdNames) {
		this.assignmentStdNames = assignmentStdNames;
	}

	public CmList<String> getLessonStdNames() {
		return lessonStdNames;
	}

	public void setLessonStdNames(CmList<String> lessonStdNames) {
		this.lessonStdNames = lessonStdNames;
	}

	public CmList<String> getQuizStdNames() {
		return quizStdNames;
	}

	public void setQuizStdNames(CmList<String> quizStdNames) {
		this.quizStdNames = quizStdNames;
	}

	public CmList<String> getUniqueStdNames() {
		return uniqueStdNames;
	}

	public void setUniqueStdNames(CmList<String> uniqueStdNames) {
		this.uniqueStdNames = uniqueStdNames;
	}
}
