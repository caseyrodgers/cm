package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class StudentActivityModel extends BaseModel implements Response {

	private static final long serialVersionUID = 6939494404062103623L;
	
	public static final String ACTIVITY_KEY       = "activity";
	public static final String IS_CUSTOM_QUIZ_KEY = "is-custom-quiz";
	public static final String IS_PASSING_KEY     = "is-passing";
	public static final String IS_QUIZ_KEY        = "is-quiz";
	public static final String LESSON_COUNT_KEY   = "lesson-count";
	public static final String LESSONS_VIEWED_KEY = "lessons-viewed";
	public static final String LESSONS_COMPLETED_KEY = "lessons-completed";
	public static final String PROGRAM_KEY        = "program";
	public static final String PROGRAM_TYPE_KEY   = "program-type";
	public static final String RESULT_KEY         = "result";
	public static final String RUN_ID_KEY         = "run-id";
	public static final String SECTION_COUNT_KEY  = "section-count";
	public static final String SECTION_NUM_KEY    = "section-num";
	public static final String START_KEY          = "start";
	public static final String STOP_KEY           = "stop";
	public static final String TEST_ID_KEY        = "test-id";
	public static final String TIME_ON_TASK_KEY   = "time-on-task";
	public static final String USE_DATE_KEY       = "use-date";
	public static final String IS_ARCHIVED        = "is-archived";
	public static final String IS_ARCHIVED_STYLE  = "is-archived-style";
	
	public String getActivity() {
		return get(ACTIVITY_KEY);
	}

	public void setActivity(String activity) {
		set(ACTIVITY_KEY, activity);
	}
	
	public void setIsCustomQuiz(Boolean yesNo) {
	    set(IS_CUSTOM_QUIZ_KEY, yesNo);
	}
	
	public Boolean getIsCustomQuiz() {
	    return get(IS_CUSTOM_QUIZ_KEY);
	}

	/** catch null program name to avoid NPE 
	 * 
	 * @return
	 */
	public String getProgramDescr() {
		return get(PROGRAM_KEY)!=null?(String)get(PROGRAM_KEY):"Unknown Program";
	}

	public void setProgramDescr(String program) {
		set(PROGRAM_KEY, program);
	}
	
	/** catch null program type to avoid NPE 
	 * 
	 * @return
	 */
	public String getProgramType() {
		return get(PROGRAM_TYPE_KEY)!=null?(String)get(PROGRAM_TYPE_KEY):"Unknown Type";
	}

	public void setProgramType(String type) {
		set(PROGRAM_TYPE_KEY, type);
	}
	
	public String getUseDate() {
		return get(USE_DATE_KEY);
	}

	public void setUseDate(String useDate) {
		set(USE_DATE_KEY, useDate);
	}

	public String getResult() {
		return get(RESULT_KEY);
	}

	public void setResult(String result) {
		set(RESULT_KEY, result);
	}

	public String getStart() {
		return get(START_KEY);
	}

	public void setStart(String start) {
		set(START_KEY, start);
	}

	public String getStop() {
		return get(STOP_KEY);
	}

	public void setStop(String stop) {
		set(STOP_KEY, stop);
	}

	public Boolean getIsQuiz() {
		return get(IS_QUIZ_KEY);
	}

	public void setIsQuiz(Boolean isQuiz) {
		set(IS_QUIZ_KEY, isQuiz);
	}

	public Boolean getIsPassing() {
		return get(IS_PASSING_KEY);
	}

	public void setIsPassing(Boolean isPassing) {
		set(IS_PASSING_KEY, isPassing);
	}

	public Integer getRunId() {
		return get(RUN_ID_KEY);
	}
	
	public void setRunId(Integer runId) {
	    set(RUN_ID_KEY, runId);	
	}

	public Integer getLessonsCompleted() {
	    return get(LESSONS_COMPLETED_KEY);
	}
	
	public void setLessonsCompleted(int completed) {
	    set(LESSONS_COMPLETED_KEY,completed);
	}
	
	public Integer getLessonCount() {
		return get(LESSON_COUNT_KEY);
	}
	
	public void setLessonCount(Integer lessonCount) {
	    set(LESSON_COUNT_KEY, lessonCount);	
	}
	
	public Integer getLessonsViewed() {
		return get(LESSONS_VIEWED_KEY);
	}
	
	public void setLessonsViewed(Integer lessonsViewed) {
	    set(LESSONS_VIEWED_KEY, lessonsViewed);	
	}

	public Integer getSectionCount() {
		return get(SECTION_COUNT_KEY);
	}
	
	public void setSectionCount(Integer sectionCount) {
	    set(SECTION_COUNT_KEY, sectionCount);	
	}
	
	public Integer getSectionNum() {
		return get(SECTION_NUM_KEY);
	}
	
	public void setSectionNum(Integer sectionNum) {
	    set(SECTION_NUM_KEY, sectionNum);	
	}
	
	public void setTestId(Integer testId) {
	    set(TEST_ID_KEY, testId);
	}
	
	public Integer getTestId() {
	    return get(TEST_ID_KEY);
	}
	
	public Integer getTimeOnTask() {
		return get(TIME_ON_TASK_KEY);
	}
	
	public void setTimeOnTask(Integer tot) {
		set(TIME_ON_TASK_KEY, tot);
	}
	
	public Integer getIsArchived() {
		return get(IS_ARCHIVED);
	}
	
	public void setIsArchived(Integer isArchived) {
		set(IS_ARCHIVED, isArchived);
		setIsArchivedStyle((isArchived!=0)?"custom-archived":null);
	}

	public String getIsArchivedStyle() {
		return get(IS_ARCHIVED_STYLE);
	}
	
	public void setIsArchivedStyle(String style) {
		set(IS_ARCHIVED_STYLE, style);
	}

	public StudentActivityModel() {
	}
}
