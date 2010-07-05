package hotmath.gwt.cm_rpc.client.rpc;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


public class PrescriptionSessionData implements IsSerializable{
	
	String topic;
	String name;
	int sessionNumber;
	boolean sessionRpa;

    public boolean isSessionRpa() {
		return sessionRpa;
	}

    /** Is this session data in RPA mode or RPP
     *  
     *  If RPA, then there are Activities for RP
     *  otherwise, it is solutions.
     *  
     * @param sessionRpa
     */
	public void setSessionRpa(boolean sessionRpa) {
		this.sessionRpa = sessionRpa;
	}

	public PrescriptionSessionData() {}
    
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
	
	
	@Override
	public String toString() {
	    return topic + " (" + sessionNumber +")";
	}
}
