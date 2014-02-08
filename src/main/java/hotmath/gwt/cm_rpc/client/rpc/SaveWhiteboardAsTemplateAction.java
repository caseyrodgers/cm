package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

public class SaveWhiteboardAsTemplateAction implements Action<RpcData> {
    
    private int uid;
    private String name;
    private String dataUrl;

    public SaveWhiteboardAsTemplateAction() {}

    public SaveWhiteboardAsTemplateAction(int uid,String name, String dataUrl) {
        this.uid = uid;
        this.name = name;
        this.dataUrl = dataUrl;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }
}
