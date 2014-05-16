package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;


public class CustomProblemLeafNode extends BaseDto {
	private CustomProblemModel customProblem;

	public CustomProblemLeafNode(String name) {
		super(__nextId(), name);
	}

	public CustomProblemLeafNode(CustomProblemModel customProblem) {
		this(customProblem.getLabel());
		this.customProblem = customProblem;
	}

	public CustomProblemModel getCustomProblem() {
		return customProblem;
	}

	public void setCustomProblem(CustomProblemModel customProblem) {
		this.customProblem = customProblem;
	}
}