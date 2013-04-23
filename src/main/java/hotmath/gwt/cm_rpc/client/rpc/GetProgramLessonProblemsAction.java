package hotmath.gwt.cm_rpc.client.rpc;

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
    
    public GetProgramLessonProblemsAction() {}
    public GetProgramLessonProblemsAction(String lesson, String file, String subject) {
        this.lesson = lesson;
        this.file = file;
        this.subject = subject;
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
        return "GetProgramLessonProblemsAction [lesson=" + lesson + ", file=" + file + ", subject=" + subject + "]";
    }

}
