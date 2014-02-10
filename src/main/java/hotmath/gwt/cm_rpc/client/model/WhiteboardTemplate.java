package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class WhiteboardTemplate implements Response {
    private String name;
    private String path;
    
    public WhiteboardTemplate() {}
    
    public WhiteboardTemplate(String name, String path) {
        this.name = name;
        this.path = path;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}
