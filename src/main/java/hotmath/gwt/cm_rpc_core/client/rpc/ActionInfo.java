package hotmath.gwt.cm_rpc_core.client.rpc;


public class ActionInfo {

        ActionType actionType = ActionType.STUDENT;

        public ActionInfo() {}

        public ActionInfo(ActionType type) {
                this.actionType = type;
        }

        public ActionType getActionType() {
                return actionType;
        }

        public void setActionType(ActionType actionType) {
                this.actionType = actionType;
        }
}
