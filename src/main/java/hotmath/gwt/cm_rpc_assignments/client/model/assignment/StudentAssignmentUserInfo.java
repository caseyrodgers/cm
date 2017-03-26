package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Represents a user's state information about individual
 *  assignments.  
 *  
 * @author casey
 *
 */
public class StudentAssignmentUserInfo implements Response {
    
    private int uid;
    private String name;
    private int assignKey;
    private Date turnInDate;
    private Date viewDateTime;
    private boolean graded;
    private boolean editable;
    private Date dueDate;
    private boolean pastDueSubmitable;
    private boolean preventLessonAccess;
    
    List<ProblemAnnotation> teacherNotes = new ArrayList<ProblemAnnotation>();

    public StudentAssignmentUserInfo(){}

    public StudentAssignmentUserInfo(int uid, int assignKey) {
        this.uid = uid;
        this.assignKey = assignKey;
    }

    public StudentAssignmentUserInfo(int uid, String name, int assignKey, Date turnInDate, boolean graded, boolean isEditable,Date viewDateTime, Date dueDate, boolean pastDueSubmitable, boolean isPreventLessonAccess, List<ProblemAnnotation> teacherNotes) {
        this(uid, assignKey);
        this.name = name;
        this.turnInDate = turnInDate;
        this.graded = graded;
        this.editable = isEditable;
        this.viewDateTime = viewDateTime;
        this.dueDate = dueDate;
        this.pastDueSubmitable = pastDueSubmitable;
        this.preventLessonAccess = isPreventLessonAccess;
        this.teacherNotes.addAll(teacherNotes);
    }

    
    public List<ProblemAnnotation> getTeacherNotes() {
		return teacherNotes;
	}
    
    public void setTeacherNotes(List<ProblemAnnotation> teacherNotes) {
		this.teacherNotes = teacherNotes;
	}
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getViewDateTime() {
        return viewDateTime;
    }

    public void setViewDateTime(Date viewDateTime) {
        this.viewDateTime = viewDateTime;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public Date getTurnInDate() {
        return turnInDate;
    }

    public void setTurnInDate(Date turnInDate) {
        this.turnInDate = turnInDate;
    }

    public boolean isGraded() {
        return graded;
    }

    public void setGraded(boolean graded) {
        this.graded = graded;
    }

    public boolean isEditable() {
        return this.editable;
    }
    
    public Date getDueDate() {
        return dueDate;
    }

    public boolean isPastDueSubmitable() {
        return pastDueSubmitable;
    }

    public boolean isPreventLessonAccess() {
        // TODO Auto-generated method stub
        return preventLessonAccess;
    }
    
}
