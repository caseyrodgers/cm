package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

/** Provide ability to get list of problems associated with a given lesson/grade level.
 * 
 * @author casey
 *
 */
public class GetProgramLessonProblemsAction implements Action<CmList<ProblemDto>>{

    String lesson;
    String file;
    String subject;
    int adminId;
    
    public GetProgramLessonProblemsAction() {}
    public GetProgramLessonProblemsAction(int adminId, String lesson, String file, String subject) {
        this.adminId = adminId;
        this.lesson = lesson;
        this.file = file;
        this.subject = subject;
    }
    

    public int getAdminId() {
        return adminId;
    }
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
    public String getLesson() {
        return lesson;
    }
    public void setLesson(String lesson) {
        this.lesson = lesson;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    @Override
    public String toString() {
        return "GetProgramLessonProblemsAction [lesson=" + lesson + ", file=" + file + ", subject=" + subject + ", adminId=" + adminId + "]";
    }

}
