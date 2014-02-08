package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

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

	@Override
	public String toString() {
		return "SaveAdminEmailAction: [ admin_id: " + adminId + ", email: " + email + " ]";
	}
}
