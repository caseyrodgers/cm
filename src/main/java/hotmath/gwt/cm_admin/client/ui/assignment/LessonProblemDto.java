package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;

@SuppressWarnings("serial")
public class LessonProblemDto extends FolderDto {

    String lessonName, problemLabel, pid;
    protected LessonProblemDto(int id, String lessonName, String problemLabel, String pid) {
        super(id, lessonName);
        this.lessonName = lessonName;
        this.problemLabel = problemLabel;
        this.pid = pid;
    }

    public LessonProblemDto(Integer id, String name) {
        super(id, name);
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getProblemLabel() {
        return problemLabel;
    }

    public void setProblemLabel(String problemLabel) {
        this.problemLabel = problemLabel;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

}
