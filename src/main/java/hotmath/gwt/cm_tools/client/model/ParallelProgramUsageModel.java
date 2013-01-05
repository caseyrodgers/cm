package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ParallelProgramUsageModel implements Response {
	
    String activity;
    boolean isQuiz;
    String result;
    String studentName;
    int userId;
    String useDate;

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

    public ParallelProgramUsageModel(String studentName, Integer userId, String activity,String result, String useDate) {
        this.studentName = studentName;
        this.userId = userId;
        this.activity = activity;
        this.result = result;
        this.useDate = useDate;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public boolean isQuiz() {
        return isQuiz;
    }

    public void setQuiz(boolean isQuiz) {
        this.isQuiz = isQuiz;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }


}