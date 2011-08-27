package hotmath.gwt.cm_rpc.client.model;


import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

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
 class PrescriptionData implements IsSerializable {
	List<SessionTopic> sessionTopics = new ArrayList<SessionTopic>();
	PrescriptionSessionData currSession;

	public PrescriptionData() {
	}

	public List<SessionTopic> getSessionTopics() {
		return sessionTopics;
	}

	public void setSessionTopics(List<SessionTopic> sessionTopics) {
		this.sessionTopics = sessionTopics;
	}

	public PrescriptionSessionData getCurrSession() {
		return currSession;
	}

	public void setCurrSession(PrescriptionSessionData currSession) {
		this.currSession = currSession;
	}
}
