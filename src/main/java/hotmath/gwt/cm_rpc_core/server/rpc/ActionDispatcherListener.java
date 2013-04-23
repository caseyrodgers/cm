package hotmath.gwt.cm_rpc_core.server.rpc;

import hotmath.cm.util.ActionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.Map;



public interface ActionDispatcherListener {
	public enum ActionExecutionType {BEFORE,AFTER};
	 void actionExecuted(ActionExecutionType type, Action<? extends Response> action);
	 Map<String,ActionInfo> getActionInfo();
}
