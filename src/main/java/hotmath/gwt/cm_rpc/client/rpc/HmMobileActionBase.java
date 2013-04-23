package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.ActionBase;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.ActionType;



public class HmMobileActionBase extends ActionBase {

        public HmMobileActionBase() {
                setActionInfo(new ActionInfo(ActionType.HM_MOBILE));
        }

}
