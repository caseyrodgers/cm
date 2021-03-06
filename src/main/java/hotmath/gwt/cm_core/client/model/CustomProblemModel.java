package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

public class CustomProblemModel implements Response {
    public static final String CUSTOM_MARKER = "Custom";
    String pid;
    TeacherIdentity teacher;
    int problemNumber;
    String comments;
    String treePath;
    
    private ProblemType problemType;
    
    private List<LessonModel> linkedLessons = new ArrayList<LessonModel>();

    public CustomProblemModel(){}
            
    public CustomProblemModel(String pid, int problemNumber, TeacherIdentity teacher, String comments, ProblemType type, String treePath) {
        this.pid = pid;
        this.problemNumber = problemNumber;
        this.teacher = teacher;

        this.comments = comments;
        this.problemType = type;
        this.treePath = treePath;
    }

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public void setLinkedLessons(List<LessonModel> linkedLessons) {
        this.linkedLessons = linkedLessons;
    }
    
    /** return single string representation of linked lessons 
     * 
     * @return
     */
    public String getLessonList() {
    	String label="";
    	if(linkedLessons.size() > 1) {
    	    label = linkedLessons.size() + "";
    	}
    	return label;
    }
    
    public int getProblemNumber() {
        return problemNumber;
    }

    public void setProblemNumber(int problemNumber) {
        this.problemNumber = problemNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setTeacher(TeacherIdentity teacher) {
        this.teacher = teacher;
    }

    public String getPid() {
        return this.pid;
    }

    public TeacherIdentity getTeacher() {
        return teacher;
    }
    
    public String getTeacherName() {
        return teacher.getTeacherName();
    }


    public ProblemType getProblemType() {
        return problemType;
    }

    public void setProblemType(ProblemType problemType) {
        this.problemType = problemType;
    }
    

    public List<LessonModel> getLinkedLessons() {
        return linkedLessons;
    }
    
    public String getFullPath() {
        String folder = treePath != null?"/" + treePath : "";
        return teacher.getTeacherName() + folder + "/" + getComments();
    }

    
    @Override
    public String toString() {
        return "CustomProblemModel [pid=" + pid + ", teacher=" + teacher + ", problemNumber=" + problemNumber + ", comments=" + comments + "]";
    }
}

