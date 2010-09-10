package hotmath.gwt.cm_rpc.client.rpc;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

import hotmath.gwt.cm_rpc.client.ClientInfo;

public class ActionBase implements Serializable,IsSerializable {
	
	private static final long serialVersionUID = -311101498643263257L;
	
	private ClientInfo clientInfo = new ClientInfo();
	
	public ActionBase() {
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

}
