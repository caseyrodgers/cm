package hotmath.gwt.cm_rpc.server.rpc;

import hotmath.cm.util.ActionInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.HashMap;
import java.util.Map;

public class ActionDispatcherListenerImplLogListener implements ActionDispatcherListener {
	
	static ActionDispatcherListenerImplLogListener __instance;
	static public ActionDispatcherListenerImplLogListener getInstance() {
		if(__instance == null)
			__instance = new ActionDispatcherListenerImplLogListener();
		return __instance;
	}
	
	public Map<String, ActionInfo> actionInfo = new HashMap<String, ActionInfo>();
	
	private ActionDispatcherListenerImplLogListener() {
		ActionDispatcher.getInstance().addActionDispatchListener(this);
	}

	public void clearActionInfo() {
		actionInfo.clear();
	}
	
	@Override
	public void actionExecuted(ActionExecutionType type,Action<? extends Response> action) {
		switch(type) {
		case BEFORE:
			ActionInfo actionInfo = getActionInfo(action.getClass().getName());
			actionInfo.setInfo(1);
			break;
			
		case AFTER:
			break;
		}
	}

	@Override
	public Map<String, ActionInfo> getActionInfo() {
		return actionInfo;
	}
	
    private ActionInfo getActionInfo(String name) {
        ActionInfo ai = actionInfo.get(name);
        if (ai == null) {
            ai = new ActionInfo(name);
            actionInfo.put(name, ai);
        }
        return ai;
    }

}
