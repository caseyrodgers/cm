package hotmath.gwt.cm_mobile3.client.event;

import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;

import com.google.gwt.event.shared.GwtEvent;

public class HandleNextFlowEvent extends GwtEvent<HandleNextFlowEventHandler>{
    public static Type<HandleNextFlowEventHandler> TYPE = new Type<HandleNextFlowEventHandler>();
    CmProgramFlowAction nextAction; 
    
    public HandleNextFlowEvent() {
    }
    
    public HandleNextFlowEvent(CmProgramFlowAction quizResult) {
        this.nextAction = quizResult;
    }
    
    @Override
    public Type<HandleNextFlowEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HandleNextFlowEventHandler handler) {
        handler.showNextAction(nextAction);
    }
}
