package hotmath.cm.server.model;

import hotmath.cm.server.model.StudentEventsDao.EventType;
import hotmath.gwt.cm_core.client.model.StudentEvent;
import hotmath.gwt.cm_core.client.model.StudentEventInfo;

import java.util.List;

import junit.framework.TestCase;

public class StudentEventsDao_Test extends TestCase {
    
    public StudentEventsDao_Test(String name) throws Exception {
        super(name);
    }
    
    public void testIt() throws Exception {
        
        int uid = 622200;
        
        StudentEventsDao d = StudentEventsDao.getInstance();
        
        
        d.addStudentEvent(uid, EventType.MESSAGE,"TestCase 1");
        
        List<String> tokens = d.getDevicesWithEvents();
        assertTrue(tokens != null);
        
        
        StudentEvent u = d.getStudentEventCurrent(uid);
        assertTrue(u != null);
        
        StudentEventInfo messages = d.getEventHistoryFor(uid);
        assertTrue(messages.getEvents().size() > 0);
        
    }

}
