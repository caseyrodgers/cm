package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.solution_editor.client.model.MathMlTransformationInfo;

public class GetMathMlTransformationAction implements Action<MathMlTransformationInfo> {
	
	private String htmlWithMathMl;

	public GetMathMlTransformationAction() {}

	public GetMathMlTransformationAction(String htmlWithMathMl) {
		this.htmlWithMathMl = htmlWithMathMl;
	}

	public String getHtmlWithMathMl() {
		return htmlWithMathMl;
	}
}
