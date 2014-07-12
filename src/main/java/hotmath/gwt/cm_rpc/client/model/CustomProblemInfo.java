package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class CustomProblemInfo implements Response {
	
	private List<CustomProblemModel> problems;
	private List<String> paths;
    private List<TeacherIdentity> teachers;

	public CustomProblemInfo(){}
	
	public CustomProblemInfo(List<CustomProblemModel> problems, List<String> paths, List<TeacherIdentity> allTeachers) {
	    this.teachers = allTeachers;
		this.problems = problems;
		this.paths = paths;
	}

	public List<TeacherIdentity> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherIdentity> teachers) {
        this.teachers = teachers;
    }

    public List<CustomProblemModel> getProblems() {
		return problems;
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	public void setProblems(List<CustomProblemModel> problems) {
		this.problems = problems;
	}
}
