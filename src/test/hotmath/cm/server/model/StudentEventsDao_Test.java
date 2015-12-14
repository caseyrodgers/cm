package hotmath.cm.server.model;

import hotmath.cm.server.model.StudentEventsDao.EventType;
import hotmath.gwt.cm_core.client.model.StudentEvent;
import hotmath.gwt.cm_core.client.model.StudentEventInfo;

import java.util.List;

import org.json.JSONObject;

import junit.framework.TestCase;

public class StudentEventsDao_Test extends TestCase {
    
    public StudentEventsDao_Test(String name) throws Exception {
        super(name);
    }
    
    int uid = 642985;
    StudentEventsDao d = StudentEventsDao.getInstance();
    
    public void testOne() throws Exception {
    	
    	/** add json message */
        d.addStudentEvent(uid, EventType.ASSIGNMENT_MESSAGE,"{uid:" + uid + ",assignment:999}");
        
        StudentEvent u = d.getStudentEventCurrent(uid);
        
        String json = u.getEventJson();
        assertNotNull(json);
        
        
        JSONObject jo = new JSONObject(json);
        JSONObject x = jo.getJSONObject("data");
        assertTrue(x.getInt("assignment") == 999);
    }
    
    public void testIt() throws Exception {
        
        d.addStudentEvent(uid, EventType.MESSAGE,"TestCase 1");
        
        List<String> tokens = d.getDevicesWithEvents();
        assertTrue(tokens != null);
        
        
        StudentEvent u = d.getStudentEventCurrent(uid);
        assertTrue(u != null);

        JSONObject jo = new JSONObject(u.getEventJson());
        String x = jo.getString("data");
        assertTrue(x.equals("TestCase 1"));

        
        StudentEventInfo messages = d.getEventHistoryFor(uid);
        assertTrue(messages.getEvents().size() > 0);
    }

}
