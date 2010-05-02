package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;


/** Base implementation that provides variables to collect
 * the active action.
 * 
 * @author casey
 *
 * @param <T>
 */
public abstract class RetryActionImplDefault<T> extends RetryAction<T> {

    public Action<? extends Response> activeAction;
    @Override
    public Action<? extends Response> getAction() {
        return activeAction;
    }
    
    public void setAction(Action<? extends Response> action) {
        this.activeAction = action;
    }
}