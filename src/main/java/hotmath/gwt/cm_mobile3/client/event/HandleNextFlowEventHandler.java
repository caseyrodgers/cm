package hotmath.gwt.cm_mobile3.client.event;

import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;

import com.google.gwt.event.shared.EventHandler;

public interface HandleNextFlowEventHandler extends EventHandler {
    void showNextAction(CmProgramFlowAction nextAction);
}
