package hotmath.gwt.cm_rpc.client.rpc;

public class ActionBase implements HasActionInfo {

        ActionInfo _actionInfo = new ActionInfo();

        @Override
        public ActionInfo getActionInfo() {
                return _actionInfo;
        }

        public void setActionInfo(ActionInfo actionInfo) {
                this._actionInfo = actionInfo;
        }

        public void setActionInfo(ActionType type) {
                this._actionInfo = new ActionInfo(type);
        }
}
