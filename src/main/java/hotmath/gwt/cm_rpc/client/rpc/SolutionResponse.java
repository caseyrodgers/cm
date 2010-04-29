package hotmath.gwt.cm_rpc.client.rpc;

public class SolutionResponse implements Response{
    
    String tutorHtml;
    boolean hasShowWork;
    
    public SolutionResponse() { }
    
    public SolutionResponse(String tutorHtml, boolean hasShowWork) {
        this.tutorHtml = tutorHtml;
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
}
