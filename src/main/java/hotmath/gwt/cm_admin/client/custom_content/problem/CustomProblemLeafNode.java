package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;


public class CustomProblemLeafNode extends BaseDto {
	private CustomProblemModel customProblem;

	public CustomProblemLeafNode(String name, BaseDto parent) {
		super(__nextId(), name);
		setParent(parent);
	}

	public CustomProblemLeafNode(CustomProblemModel customProblem, FolderDto parent) {
		this(customProblem.getProblemNumber() + "", parent);
		this.customProblem = customProblem;
	}

	public CustomProblemModel getCustomProblem() {
		return customProblem;
	}

	public void setCustomProblem(CustomProblemModel customProblem) {
		this.customProblem = customProblem;
	}
}