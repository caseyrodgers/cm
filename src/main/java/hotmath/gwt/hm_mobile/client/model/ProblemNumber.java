package hotmath.gwt.hm_mobile.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ProblemNumber implements Response {
	
	String problem;
	String problemSet;
	String pid;
	
	public ProblemNumber() {}
	
	public ProblemNumber(String problem, String problemSet, String pid) {
		this.problem = problem;
		this.problemSet = problemSet;
		this.pid = pid;
	}

	public String getProblem() {
    	return problem;
    }

	public void setProblem(String problem) {
    	this.problem = problem;
    }

	public String getPid() {
    	return pid;
    }

	public void setPid(String pid) {
    	this.pid = pid;
    }

	public String getProblemSet() {
    	return problemSet;
    }

	public void setProblemSet(String problemSet) {
    	this.problemSet = problemSet;
    }

	@Override
    public String toString() {
	    return "ProblemNumber [problem=" + problem + ", problemSet=" + problemSet + ", pid=" + pid + "]";
    }

}
