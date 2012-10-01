package hotmath.gwt.cm_tutor.client.event;

import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;

import com.google.gwt.event.shared.GwtEvent;

/** Fired when a solution has been loaded and is ready
 * for interaction.
 * 
 * 
 * @author casey
 *
 */
public class SolutionHasBeenLoadedEvent extends GwtEvent<SolutionHasBeenLoadedHandler> {

    SolutionInfo solutionInfo;
    
    public SolutionHasBeenLoadedEvent(SolutionInfo info) {
        this.solutionInfo = info;
    }
    
    public static Type<SolutionHasBeenLoadedHandler> TYPE = new Type<SolutionHasBeenLoadedHandler>();
    
    @Override
    public Type<SolutionHasBeenLoadedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SolutionHasBeenLoadedHandler handler) {
        handler.solutionLoaded(solutionInfo);
    }
}
