package hotmath.gwt.shared.client.rpc;




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
        this.webBase = webBase;
        
        // make sure has trailing slash
        if(!fileBase.endsWith("/"))
            fileBase += "/";
        
        this.fileBase = fileBase;
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
        return webBase + name;
    }
}
