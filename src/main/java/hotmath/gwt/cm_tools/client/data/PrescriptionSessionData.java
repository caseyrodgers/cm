package hotmath.gwt.cm_tools.client.data;

import java.util.ArrayList;
import java.util.List;


public class PrescriptionSessionData {
	
	String topic;
	String name;
	int sessionNumber;
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	
	public int getSessionNumber() {
		return sessionNumber;
	}

	public void setSessionNumber(int sessionNumber) {
		this.sessionNumber = sessionNumber;
	}

	List<PrescriptionSessionDataResource> inmhResources = new ArrayList<PrescriptionSessionDataResource>();
	
	public PrescriptionSessionData() {}
	
	public List<PrescriptionSessionDataResource> getInmhResources() {
		return inmhResources;
	}

	public void setInmhResources(List<PrescriptionSessionDataResource> inmhItems) {
		this.inmhResources = inmhItems;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
