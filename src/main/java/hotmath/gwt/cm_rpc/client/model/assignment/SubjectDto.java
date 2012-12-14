package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class SubjectDto extends FolderDto implements Response {

    String subject;
    String subjectLabel;
    int testDefId;
    List<SectionDto> lessons = new ArrayList<SectionDto>();
    
    public SubjectDto(){}
    
    public SubjectDto(int id, int testDefId, String subject, String subjectLabel) {
        super(id, subjectLabel);
        this.testDefId = testDefId;
        this.subject = subject;
        this.subjectLabel = subjectLabel;
    }
    
    public int getTestDefId() {
        return testDefId;
    }

    public void setTestDefId(int testDefId) {
        this.testDefId = testDefId;
    }

    public List<SectionDto> getLessons() {
        return lessons;
    }

    public void setLessons(List<SectionDto> lessons) {
        this.lessons = lessons;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public List<SectionDto> getSections() {
        return lessons;
    }
    public void setSection(List<SectionDto> lessons) {
        this.lessons = lessons;
    }

    public String getSubjectLabel() {
        return subjectLabel;
    }

    public void setSubjectLabel(String subjectLabel) {
        this.subjectLabel = subjectLabel;
    }
    
}
