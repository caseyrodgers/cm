package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class LessonDto extends FolderDto implements Response {
    
    String lessonName;
    String lessonFile;
    int testDefId;
    String subject;
    
    List<ProblemDto> problems = new ArrayList<ProblemDto>();
    
    public LessonDto() {}
    
    public LessonDto(int id, int testDefId,String subject,String lessonName, String lessonFile) {
        super(id, lessonName);
        this.testDefId = testDefId;
        this.subject = subject;
        this.lessonName = lessonName;
        this.lessonFile = lessonFile;
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

    public String getLessonFile() {
        return lessonFile;
    }

    public void setLessonFile(String lessonFile) {
        this.lessonFile = lessonFile;
    }

    @Override
    public String toString() {
        return "LessonDto [lessonName=" + lessonName + ", lessonFile=" + lessonFile + ", testDefId=" + testDefId
                + ", subject=" + subject + ", problems=" + problems + "]";
    }

}
