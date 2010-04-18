package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.util.RpcData;

public class SetBackgroundStyleAction implements Action<RpcData>{
    
    Integer uid;
    String styleName;
    
    public SetBackgroundStyleAction(){}
    
    public SetBackgroundStyleAction(Integer uid, String styleName) {
        this.uid = uid;
        this.styleName = styleName;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

}
