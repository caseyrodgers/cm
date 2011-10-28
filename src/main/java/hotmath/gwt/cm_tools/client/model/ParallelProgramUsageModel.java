package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ParallelProgramUsageModel extends BaseModel implements Response {
	
	public static final String STUDENT_NAME  = "studentName";
	public static final String ACTIVITY      = "activity";
	public static final String RESULT        = "result";
	public static final String USER_ID       = "userId";
	public static final String USE_DATE      = "useDate";
	public static final String IS_QUIZ       = "isQuiz";

	public ParallelProgramUsageModel(){
        /** empty */
    }
	
	/**
	 * @param studentName
	 * @param userId
	 * @param activity
	 * @param result
	 * @param useDate
	 */

    public ParallelProgramUsageModel(String studentName, Integer userId, String activity,
    	String result, String useDate) {
        setStudentName(studentName);
        setUserId(userId);
        setActivity(activity);
        setResult(result);
        setUseDate(useDate);
    }

    public void setStudentName(String name) {
        set(STUDENT_NAME, name);
    }

    public String getStudentName() {
        return get(STUDENT_NAME);
    }

    public void setUserId(Integer userId) {
        set(USER_ID, userId);
    }

    public Integer getUserId() {
        return get(USER_ID);
    }

    public void setActivity(String activity) {
        set(ACTIVITY, activity);
    }

    public String getActivity() {
        return get(ACTIVITY);
    }

    public void setResult(String result) {
        set(RESULT, result);
    }

    public String getResult() {
        return get(RESULT);
    }

    public void setUseDate(String result) {
        set(USE_DATE, result);
    }

    public String getUseDate() {
        return get(USE_DATE);
    }

    public void setIsQuiz(Boolean isQuiz) {
    	set(IS_QUIZ, isQuiz);
    }

    public Boolean getIsQuiz() {
        return get(IS_QUIZ);
    }

}