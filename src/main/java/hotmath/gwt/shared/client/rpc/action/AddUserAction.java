package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelI;

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
