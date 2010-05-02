package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.MultiActionRequestAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

/** provide ability to execute multiple actions in single request
 * 
 * Add all actions to execute:
 *   1. getActions().add(ac1), etc....
 *   2. call executeActions();
 *   3. implement the processResponses method 
 *   that will contain a list of response objects
 *   in order of added actions.
 * 
 * @author casey
 *
 */
abstract public class MultiActionWrapper {
    
    abstract public void processResponses(List<Response> responses);
    
    List<Action<? extends Response>> actions = new ArrayList<Action<? extends Response>>();
    public MultiActionWrapper() {}

    public List<Action<? extends Response>> getActions() {
        return actions;
    }

    public void setActions(List<Action<? extends Response>> actions) {
        this.actions = actions;
    }

    public void executeActions() {
        new RetryAction<CmList<Response>>() {
            @Override
            public void attempt() {
                MultiActionRequestAction action = new MultiActionRequestAction();
                action.getActions().addAll(actions);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }

            @Override
            public void oncapture(CmList<Response> value) {
            }
        }.register();
    }
}
