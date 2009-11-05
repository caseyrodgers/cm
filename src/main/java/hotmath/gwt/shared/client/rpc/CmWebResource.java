package hotmath.gwt.shared.client.rpc;

import hotmath.gwt.shared.client.CmShared;




/** A web resource is a resource that can be accessed
 *  via the file system with an absolute path, and via
 *  a URL from a web server.
 *  
 * @author casey
 *
 */
public class CmWebResource implements Response {

	String file;
    String fileBase;
    String webBase;
    
    public CmWebResource() {}
    
    public CmWebResource(String file, String fileBase, String webBase) {
        this.file = file;
        this.fileBase = fileBase;
        
        // make sure has trailing slash
        if(!webBase.endsWith("/"))
            webBase += "/";
        if(!webBase.startsWith("/") && !webBase.startsWith("http"))
            webBase = "/" + webBase;
        
        this.webBase = webBase;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    /** Return the web HTTP url that can be used to access this resource
     * 
     * @return
     */
    public String getUrl() {
        // get the last component as the name
    	String name;
    	if (fileBase != null && file.startsWith(fileBase)) {
    		name = file.substring(fileBase.length());
    	}
    	else {
            String p[] = file.split("/");
            name = p[p.length-1];
    	}
    	
    	// include path to catchup server domain
        String ret = CmShared.CM_HOME_URL + webBase + name;
        return ret;
    }
}
