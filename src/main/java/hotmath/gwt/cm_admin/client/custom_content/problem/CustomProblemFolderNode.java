package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;



public class CustomProblemFolderNode extends FolderDto {
	public CustomProblemFolderNode(String name) {
		super(__nextId(), name);
	}

	public String getFolderName() {
		return getName();
	}
}
