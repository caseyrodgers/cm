package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class AddUserAction implements Action<StudentModelI>{
	
	StudentModelI model;
	
	public AddUserAction() {}
	
	public AddUserAction(StudentModelI model) {
		this.model = model;
	}

	public StudentModelI getModel() {
		return model;
	}

	public void setModel(StudentModelI model) {
		this.model = model;
	}
}
