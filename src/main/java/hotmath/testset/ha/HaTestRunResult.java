package hotmath.testset.ha;

public class HaTestRunResult {
	Integer resultId;
	String pid;   // the solution pid
	String result; // the answer result (correct,incorrect,unanswered)
	int responseIndex;  // the index of the selected response

	
	public int getResponseIndex() {
        return responseIndex;
    }
    public void setResponseIndex(int responseIndex) {
        this.responseIndex = responseIndex;
    }
    public Integer getResultId() {
		return resultId;
	}
	public void setResultId(Integer resultId) {
		this.resultId = resultId;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	/** Return true if this result is correct
	 * 
	 * @return
	 */
	public boolean isCorrect() {
	    return (result != null && result.equals("Correct"))?true:false;
	}
	
	public String toString() {
		return resultId + "," + pid + "," + responseIndex + ", " + result;
	}
}
