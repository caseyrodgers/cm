package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;

/** Provide ability to get list of problems associated with a given lesson/grade level.
 * 
 * @author casey
 *
 */
public class GetProgramLessonProblemsAction implements Action<CmList<ProblemDto>>{

    String lesson;
    String subject;
    
    public GetProgramLessonProblemsAction() {}
    public GetProgramLessonProblemsAction(String lesson, String subject) {
        this.lesson = lesson;
        this.subject = subject;
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
        return "GetProgramLessonProblemsAction [lesson=" + lesson + ", subject=" + subject + "]";
    }

}
