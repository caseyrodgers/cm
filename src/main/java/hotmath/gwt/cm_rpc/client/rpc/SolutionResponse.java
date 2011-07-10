package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.ProblemNumber;


public class SolutionResponse implements Response{
    
    String tutorHtml;
    String solutionData;
    boolean hasShowWork;
    ProblemNumber problem;

    public SolutionResponse() { }
    
    public SolutionResponse(ProblemNumber problem, String tutorHtml, String solutionData, boolean hasShowWork) {
    	this.problem = problem;
        this.tutorHtml = tutorHtml;
        this.solutionData = solutionData;        
        this.hasShowWork = hasShowWork;
    }

    public String getTutorHtml() {
        return tutorHtml;
    }

    public void setTutorHtml(String tutorHtml) {
        this.tutorHtml = tutorHtml;
    }

    public boolean isHasShowWork() {
        return hasShowWork;
    }

    public void setHasShowWork(boolean hasShowWork) {
        this.hasShowWork = hasShowWork;
    }

    public String getSolutionData() {
        return solutionData;
    }

    public void setSolutionData(String solutionData) {
        this.solutionData = solutionData;
    }

	public ProblemNumber getProblem() {
		return problem;
	}

	public void setProblem(ProblemNumber problem) {
		this.problem = problem;
	}
}
