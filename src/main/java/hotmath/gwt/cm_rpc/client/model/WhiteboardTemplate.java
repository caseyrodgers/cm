package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class WhiteboardTemplate implements Response {
    private String path;
    private int adminId;
    
    public WhiteboardTemplate() {}
    
    /** admin_id set to 0 for system template
     * 
     * @param adminId
     * @param path
     */
    public WhiteboardTemplate(int adminId, String path) {
    	this.adminId = adminId;
        this.path = path;
    }
    public WhiteboardTemplate(String path) {
    	this(0, path);
    }

    public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    
    public boolean isSystemTemplate() {
    	return adminId == 0;
    }
}
