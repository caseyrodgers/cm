package hotmath.gwt.cm_tutor.client.event;

import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;

import com.google.gwt.event.shared.EventHandler;

public interface SolutionHasBeenLoadedHandler extends EventHandler {
    
    void solutionLoaded(SolutionInfo solutionInfo);

}
