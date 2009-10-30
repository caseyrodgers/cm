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
    String webBase;
    
    public CmWebResource() {}
    
    public CmWebResource(String file, String webBase) {
        this.file = file;
        this.webBase = webBase;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
    
    public String getUrl() {
        
        // get the last component as the name
        String p[] = file.split("/");
        String name = p[p.length-1];
        
        return webBase + "/" + name;
    }
}
