package hotmath.gwt.cm_rpc.client.rpc;


import java.util.ArrayList;
import java.util.List;

public class MultiActionRequestAction implements Action<CmList<Response>>{

    List<Action<? extends Response>> actions = new ArrayList<Action<? extends Response>>();
    public MultiActionRequestAction() {}

    public List<Action<? extends Response>> getActions() {
        return actions;
    }

    public void setActions(List<Action<? extends Response>> actions) {
        this.actions = actions;
    }

    public MultiActionRequestAction(List<Action<? extends Response>> actions) {
        this.actions.addAll(actions);
    }
    
    @Override
    public String toString() {
        return "MultiActionRequestAction [actions=" + actions + "]";
    }
}
