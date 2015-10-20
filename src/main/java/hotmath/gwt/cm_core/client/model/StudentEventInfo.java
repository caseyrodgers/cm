package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class StudentEventInfo implements Response {

    private List<StudentEvent> events;

    public StudentEventInfo(List<StudentEvent> events) {
        this.events = events;
    }

    public List<StudentEvent> getEvents() {
        return events;
    }

    public void setEvents(List<StudentEvent> events) {
        this.events = events;
    }

}
