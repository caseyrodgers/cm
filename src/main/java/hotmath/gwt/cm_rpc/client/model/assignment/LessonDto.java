package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.FolderDto;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class LessonDto extends FolderDto implements Response {
    
    String lessonName;
    int testDefId;
    String subject;
    
    List<ProblemDto> problems = new ArrayList<ProblemDto>();
    
    public LessonDto(int id, int testDefId,String subject,String lessonName) {
        super(id, lessonName);
        this.testDefId = testDefId;
        this.subject = subject;
        this.lessonName = lessonName;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    private List<ProblemDto> getProblems() {
        return problems;
    }

    private void setProblems(List<ProblemDto> problems) {
        this.problems = problems;
    }

    public int getTestDefId() {
        return testDefId;
    }

    public void setTestDefId(int testDefId) {
        this.testDefId = testDefId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


}
