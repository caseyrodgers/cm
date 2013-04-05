package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.List;

/** Class to contain all problem assignments for a single user
 *  in a single assignment.
 *  
 *  NOTE: This is isolated to allow tracking this data in a
 *  separate class so that StudentAssignment can be more
 *  lightweight.
 *  
 * @author casey
 *
 */
public class StudentAssignmentStatuses implements Response {

    private StudentAssignment studentAssignment;
    private CmList<StudentProblemDto> assigmentStatuses;
    private CmList<StudentLessonDto> lessonStatuses;

    public StudentAssignmentStatuses(){}
    
    public StudentAssignmentStatuses(StudentAssignment studentAssignment, CmList<StudentProblemDto> assigmentStatuses,CmList<StudentLessonDto> lessonStatuses) {
        this.studentAssignment = studentAssignment;
        this.assigmentStatuses = assigmentStatuses;
        this.lessonStatuses = lessonStatuses;
    }

    public StudentAssignment getStudentAssignment() {
        return studentAssignment;
    }

    public void setStudentAssignment(StudentAssignment studentAssignment) {
        this.studentAssignment = studentAssignment;
    }

    public List<StudentProblemDto> getAssigmentStatuses() {
        return assigmentStatuses;
    }

    public void setAssigmentStatuses(CmList<StudentProblemDto> assigmentStatuses) {
        this.assigmentStatuses = assigmentStatuses;
    }

    public List<StudentLessonDto> getLessonStatuses() {
        return lessonStatuses;
    }

    public void setLessonStatuses(CmList<StudentLessonDto> lessonStatuses) {
        this.lessonStatuses = lessonStatuses;
    }
    
    /** Return true if this Assignment is 
     *  considered complete from the student's
     *  point of view.
     *  
     *  
     * @return
     */
    public boolean isComplete() {
        for(StudentProblemDto spd: assigmentStatuses) {
            if(!spd.isComplete()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        return "StudentAssignmentStatuses [studentAssignment=" + studentAssignment + ", assigmentStatuses=" + assigmentStatuses + ", lessonStatuses="
                + lessonStatuses + "]";
    }
}
