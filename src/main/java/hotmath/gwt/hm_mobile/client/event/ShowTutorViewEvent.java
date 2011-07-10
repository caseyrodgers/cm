package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_rpc.client.model.ProblemNumber;

import com.google.gwt.event.shared.GwtEvent;

public class ShowTutorViewEvent extends GwtEvent<ShowTutorViewEventHandler> {
    
    ProblemNumber problem;
    

    public ShowTutorViewEvent(ProblemNumber problem) {
        this.problem = problem;
    }
    
    public static Type<ShowTutorViewEventHandler> TYPE = new Type<ShowTutorViewEventHandler>();
    
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowTutorViewEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowTutorViewEventHandler handler) {
        handler.showTutor(problem);
    }
}
