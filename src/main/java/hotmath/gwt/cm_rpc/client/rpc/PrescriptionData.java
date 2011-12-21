package hotmath.gwt.cm_rpc.client.rpc;


import hotmath.gwt.cm_rpc.client.model.SessionTopic;

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
	List<SessionTopic> sessionTopics = new ArrayList<SessionTopic>();
	PrescriptionSessionData currSession;

	public PrescriptionData() {
		// 
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
	
	public void setLessonAsComplete(String lesson) {
        for(SessionTopic topic: sessionTopics) {
            if(topic.getTopic().equals(lesson)) {
                topic.setComplete(true);
            }
        }
	}
	
	public int getCountCompletedTopics() {
	    int cnt=0;
	    for(SessionTopic topic: sessionTopics) {
	        if(topic.isComplete()) {
	            cnt++;
	        }
	    }
	    return cnt;
	}
	
	public boolean areAllLessonsCompleted() {
	    return getCountCompletedTopics() == sessionTopics.size();
	}
}
