package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class AssignmentLessonData implements Response {
    
    List<SubjectDto> subjects = new ArrayList<SubjectDto>();
    
    public List<SubjectDto> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<SubjectDto> subjects) {
        this.subjects = subjects;
    }

    public AssignmentLessonData(){}
}
