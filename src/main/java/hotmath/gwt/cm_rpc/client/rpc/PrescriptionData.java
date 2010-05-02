package hotmath.gwt.cm_rpc.client.rpc;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Parent session data, containing a single active session used to encapsulate
 * session data
 * 
 * 
 * @author Casey
 * 
 */
public class PrescriptionData implements IsSerializable {
	List<String> sessionTopics = new ArrayList<String>();
	PrescriptionSessionData currSession;

	public PrescriptionData() {
		// 
	}

	public List<String> getSessionTopics() {
		return sessionTopics;
	}

	public void setSessionTopics(List<String> sessionTopics) {
		this.sessionTopics = sessionTopics;
	}

	public PrescriptionSessionData getCurrSession() {
		return currSession;
	}

	public void setCurrSession(PrescriptionSessionData currSession) {
		this.currSession = currSession;
	}
}
