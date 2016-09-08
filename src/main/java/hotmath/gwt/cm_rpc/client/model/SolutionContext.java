package hotmath.gwt.cm_rpc.client.model;

import java.io.Serializable;

public class SolutionContext implements Serializable {
    
    String contextJson;
    String contextGuid;
    int probNum;
    String pid;
    
    
    int countInUse;   // count of use in the wild (assigments)

    public SolutionContext() {}
    public SolutionContext(String pid, int probNum, String contextJson) {
        this.pid = pid;
        this.probNum = probNum;
        this.contextJson = contextJson;
    }
    public String getContextJson() {
        return contextJson;
    }
    public void setContextJson(String contextJson) {
        this.contextJson = contextJson;
    }

    public int getProbNum() {
        return probNum;
    }
    public void setProbNum(int probNum) {
        this.probNum = probNum;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    
    public int getCountInUse() {
		return countInUse;
	}
    
    public void setCountInUse(int countInUse) {
		this.countInUse = countInUse;
	}
    
    
    @Override
    public String toString() {
        return "SolutionContext [contextJson=" + contextJson + ", probNum=" + probNum + ", pid=" + pid + "]";
    }
   
}
