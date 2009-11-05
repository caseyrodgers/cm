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

        /** hardcoded for now .. need way to be server independent
            this is needed to cross from the hotmath domain into 
            the catchupmath domain to aquire the resource.  Needs
            to be a per-instanbce configuration parameter
	*/
        return "http://catchupmath.com/" + webBase + name;
    }
}
