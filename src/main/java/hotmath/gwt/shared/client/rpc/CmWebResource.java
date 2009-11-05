package hotmath.gwt.shared.client.rpc;

import com.allen_sauer.gwt.log.client.Log;

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
     * The URL returned should be an absolute URL.
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
    	
    	
    	/** remove any Windows nastiness with filenames
    	 * 
    	 */
    	name = name.replaceAll("\\\\", "/");

        String ret = CmShared.CM_HOME_URL + webBase + name;
        Log.info("CmWebResource: return URL: " + ret);
        return ret;
    }
}
