package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class CustomProblemInfo implements Response {
	
	private List<CustomProblemModel> problems;
	private List<String> paths;

	public CustomProblemInfo(){}
	
	public CustomProblemInfo(List<CustomProblemModel> problems, List<String> paths) {
		this.problems = problems;
		this.paths = paths;
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
