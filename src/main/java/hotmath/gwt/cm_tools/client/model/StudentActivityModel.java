package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class StudentActivityModel extends BaseModel implements Response {

	private static final long serialVersionUID = 6939494404062103623L;
	
	public static final String ACTIVITY_KEY = "activity";
	public static final String PROGRAM_KEY = "program";
	public static final String USE_DATE_KEY = "use-date";
	public static final String RESULT_KEY = "result";
	public static final String START_KEY = "start";
	public static final String STOP_KEY = "stop";
	public static final String IS_QUIZ_KEY = "is-quiz";
	public static final String RUN_ID_KEY = "run-id";
	public static final String TEST_ID_KEY = "test-id";
	public static final String IS_CUSTOM_QUIZ_KEY = "is-custom-quiz";
	public static final String TIME_ON_TASK_KEY = "time-on-task";
	
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

	/** catch null program name to avoid nullexception 
	 * 
	 * @return
	 */
	public String getProgramDescr() {
		return get(PROGRAM_KEY)!=null?(String)get(PROGRAM_KEY):"Unknown Program";
	}

	public void setProgramDescr(String program) {
		set(PROGRAM_KEY, program);
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

	public Integer getRunId() {
		return get(RUN_ID_KEY);
	}
	
	public void setRunId(Integer runId) {
	    set(RUN_ID_KEY, runId);	
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
	
	public StudentActivityModel() {
	}
}
