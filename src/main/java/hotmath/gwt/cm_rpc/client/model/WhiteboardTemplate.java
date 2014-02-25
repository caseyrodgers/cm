package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class WhiteboardTemplate implements Response {
    private String path;
    
    public WhiteboardTemplate() {}
    
    public WhiteboardTemplate(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
