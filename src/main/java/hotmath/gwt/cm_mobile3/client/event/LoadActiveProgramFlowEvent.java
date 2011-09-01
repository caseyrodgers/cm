package hotmath.gwt.cm_mobile3.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class LoadActiveProgramFlowEvent extends GwtEvent<LoadActiveProgramFlowEventHandler>{

    public static Type<LoadActiveProgramFlowEventHandler> TYPE = new Type<LoadActiveProgramFlowEventHandler>();
    
    public LoadActiveProgramFlowEvent() {
    }
    
    @Override
    public Type<LoadActiveProgramFlowEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoadActiveProgramFlowEventHandler handler) {
        handler.loadActiveProgramFlow();
    }
}
