package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StringHolder;

public class SaveAdminEmailAction implements Action<StringHolder>{
	
	int adminId;
	String email;
	
	public SaveAdminEmailAction() {
		//
	}
	
	public SaveAdminEmailAction(int adminId, String email) {
		this.adminId = adminId;
		this.email = email;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
