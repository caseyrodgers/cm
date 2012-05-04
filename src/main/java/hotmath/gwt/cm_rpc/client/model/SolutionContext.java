package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_tools.client.ui.CmLogger;

import java.io.Serializable;
import java.util.List;

public class SolutionContext implements Serializable {
    
    String contextJson;
    int probNum;
    String pid;

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
    
    @Override
    public String toString() {
        return "SolutionContext [contextJson=" + contextJson + ", probNum=" + probNum + ", pid=" + pid + "]";
    }
    
    /** Return the variable solution problem number JSON
     *  or null if problem number does not exist.
     *  
     * @param contexts
     * @param probNum
     * @return
     */
    public static String getSolutionContext(List<SolutionContext> contexts, int probNum) {
        String res=null;
        for(SolutionContext sc: contexts) {
            if(sc.getProbNum() == probNum) {
                CmLogger.debug("SolutionContext: getSolutionContext found: " + sc);
                res = sc.getContextJson();
            }
        }
        return res;
    }    
}
