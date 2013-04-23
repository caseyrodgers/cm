package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class StudentLessonDto implements Response {

    private int uid;
    private String lessonName;
    private String status;
    
    public StudentLessonDto() {
    }

    public StudentLessonDto(int uid, String lessonName, String status) {
        this.uid = uid;
        this.lessonName = lessonName;
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }


    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StudentLessonDto [uid=" + uid + ", lessonName=" + lessonName + ", status=" + status + "]";
    }
}
