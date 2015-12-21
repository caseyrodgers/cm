package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.shared.server.service.command.NewMobileUserCommand;

/**
 * Start a new mobile user by:
 * 
 * 1. create new user 2. assign to auto placement test.
 * 
 * @author casey
 *
 */
public class NewMobileUserAction implements Action<RpcData> {
	private String deviceId;

	public NewMobileUserAction() {
	}

	public NewMobileUserAction(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public String toString() {
		return "NewMobileUserAction [deviceId=" + deviceId + "]";
	}

}
