package hotmath.gwt.shared.client.eventbus;

abstract public class CmEventListenerImplDefault implements CmEventListener {

    abstract public void handleEvent(CmEvent event);
    
    public String getEventOfInterest() {
        return null;
    }
}
