package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ProblemDto extends BaseDto implements Response {
    
    static public enum ProblemType {INPUT_WIDGET,MULTI_CHOICE,WHITEBOARD}; 
    public ProblemDto(){}
    
    String label, pid, lesson;
    ProblemType problemType;
    public ProblemDto(int id, String lesson, String label, String pid) {
        super(id, label);
        this.label = label;
        this.lesson = lesson;
        this.pid = pid;
    }

    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "ProblemDto [label=" + label + ", pid=" + pid + ", lesson=" + lesson + ", problemType=" + problemType
                + "]";
    }

}
