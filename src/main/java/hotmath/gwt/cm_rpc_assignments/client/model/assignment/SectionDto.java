package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class SectionDto extends FolderDto implements Response {

    int section;
    int testDefId;
    String subject;
    List<LessonDto> lessons = new ArrayList<LessonDto>();
    
    public SectionDto(int id, int testDefId, String subject, int section) {
        super(id, "Section: " + section);
        this.testDefId = testDefId;
        this.subject = subject;
        this.section = section;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public List<LessonDto> getLessons() {
        return lessons;
    }
    public void setLessons(List<LessonDto> lessons) {
        this.lessons = lessons;
    }
    
    public int getTestDefId() {
        return testDefId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTestDefId(int testDefId) {
        this.testDefId = testDefId;
    }
    
}
