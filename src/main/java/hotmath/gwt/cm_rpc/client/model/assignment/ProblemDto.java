package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ProblemDto extends BaseDto implements Response {
    
    public ProblemDto(){}
    
    String label, pid, lesson;
    public ProblemDto(int id, String lesson, String label, String pid) {
        super(id, label);
        this.label = label;
        this.lesson = lesson;
        this.pid = pid;
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

}
