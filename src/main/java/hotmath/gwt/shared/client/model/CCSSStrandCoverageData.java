package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSStrandCoverageData implements Response {

	private static final long serialVersionUID = 691408127308605471L;

	int count;
	int sequenceNum;
	boolean asAssignment;
	boolean asLesson;
	boolean asQuiz;
	String levelName;

	public CCSSStrandCoverageData() {
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public boolean isAsAssignment() {
		return asAssignment;
	}

	public void setAsAssignment(boolean asAssignment) {
		this.asAssignment = asAssignment;
	}

	public boolean isAsLesson() {
		return asLesson;
	}

	public void setAsLesson(boolean asLesson) {
		this.asLesson = asLesson;
	}

	public boolean isAsQuiz() {
		return asQuiz;
	}

	public void setAsQuiz(boolean asQuiz) {
		this.asQuiz = asQuiz;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

}
