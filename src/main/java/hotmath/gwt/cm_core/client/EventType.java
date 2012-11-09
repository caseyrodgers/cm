package hotmath.gwt.cm_core.client;

public class EventType {
    String name;

    public EventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof EventType) {
            EventType et = (EventType)obj;
            return et.getName().equals(getName());
        }
        else {
            return super.equals(obj);
        }
    }
}