package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class StudentActivityModel  implements Response {

	private static final long serialVersionUID = -7576878140605684669L;

	static int __idKey;
	
	int id;
	String activity;
	boolean isAssignment;
	boolean isCustomQuiz;
	boolean isPassing;
	boolean isQuiz;
	int lessonCount;
	int lessonsViewed;
	int lessonsCompleted;
	String program;
	String programDescr;
	String programType;
	String result;
	int  runId;
	int sectionCount;
	int sectionNum;
	String start;
	String stop;
	int testId;
	int activeTime;
	String useDate;
	boolean isArchived;
	String isArchivedStyle;

	
	public StudentActivityModel(){
		this.id = __idKey++;
	}

	public int getId() {
		return id;
	}

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public boolean isAssignment() {
		return isAssignment;
	}

	public void setAssignment(boolean isAssignment) {
		this.isAssignment = isAssignment;
	}

	public boolean isCustomQuiz() {
        return isCustomQuiz;
    }

    public void setCustomQuiz(boolean isCustomQuiz) {
        this.isCustomQuiz = isCustomQuiz;
    }

    public boolean isPassing() {
        return isPassing;
    }

    public void setPassing(boolean isPassing) {
        this.isPassing = isPassing;
    }

    public boolean isQuiz() {
        return isQuiz;
    }

    public void setQuiz(boolean isQuiz) {
        this.isQuiz = isQuiz;
    }

    public int getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(int lessonCount) {
        this.lessonCount = lessonCount;
    }

    public int getLessonsViewed() {
        return lessonsViewed;
    }

    public void setLessonsViewed(int lessonsViewed) {
        this.lessonsViewed = lessonsViewed;
    }

    public int getLessonsCompleted() {
        return lessonsCompleted;
    }


    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getProgramDescr() {
        return programDescr;
    }

    public void setProgramDescr(String programDescr) {
        this.programDescr = programDescr;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }

    public int getSectionNum() {
        return sectionNum;
    }

    public void setSectionNum(int sectionNum) {
        this.sectionNum = sectionNum;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(int timeOnTask) {
        this.activeTime = timeOnTask;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }

    public String getIsArchivedStyle() {
        return isArchivedStyle;
    }

    public void setIsArchivedStyle(String isArchivedStyle) {
        this.isArchivedStyle = isArchivedStyle;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLessonsCompleted(int lessonsCompleted) {
        this.lessonsCompleted = lessonsCompleted;
    }
    
   
    /** return true if named model is a custom problem.
     * 
     * @param sam
     * @return
     */
    static public boolean isCustomProblem(StudentActivityModel sam) {
    	// avoid NPE
    	if (sam == null || sam.getProgram() == null) {
    		return false;
    	}
        String name = sam.getProgram().trim().toLowerCase();
        if(name.startsWith("cp") || name.startsWith("sp")) {
            return true;
        }
        return false;
    }
    
}
