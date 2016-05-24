package hotmath.gwt.solution_editor.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class MathMlTransformationInfo implements Response{

	private String logMessages;
	private String results;

	public MathMlTransformationInfo() {}
	
	public MathMlTransformationInfo(String results, String logMessages) {
		this.results = results;
		this.logMessages = logMessages;
	}

	public String getLogMessages() {
		return logMessages;
	}
	
	public String getResults() {
		return results;
	}
}
