package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.shared.client.rpc.Response;

/** Provide information about current build version numbers
 *  for CM
 *  
 * @author casey
 *
 */
public class CmVersionInfo implements Response{
    
    String gwtBuildDateStamp;
    int gwtBuildVersion;
    String webappBuildDateStamp;
    int webappBuildVersion;
    
    public CmVersionInfo() {}
    
    public CmVersionInfo(Integer gwtVersion, String gwtDateStamp,Integer webVersion, String webDateStamp) {
        this.gwtBuildVersion = gwtVersion;
        this.gwtBuildDateStamp = gwtDateStamp;
        this.webappBuildVersion = webVersion;
        this.webappBuildDateStamp = webDateStamp;
    }
    
    public String getGwtBuildDateStamp() {
        return gwtBuildDateStamp;
    }
    public void setGwtBuildDateStamp(String gwtBuildDateStamp) {
        this.gwtBuildDateStamp = gwtBuildDateStamp;
    }
    public int getGwtBuildVersion() {
        return gwtBuildVersion;
    }
    public void setGwtBuildVersion(int gwtBuildVersion) {
        this.gwtBuildVersion = gwtBuildVersion;
    }
    public String getWebappBuildDateStamp() {
        return webappBuildDateStamp;
    }
    public void setWebappBuildDateStamp(String webappBuildDateStamp) {
        this.webappBuildDateStamp = webappBuildDateStamp;
    }
    public int getWebappBuildVersion() {
        return webappBuildVersion;
    }
    public void setWebappBuildVersion(int webappBuildVersion) {
        this.webappBuildVersion = webappBuildVersion;
    }
    
    public String toString() {
        String s = "c=" + gwtBuildVersion + " " + gwtBuildDateStamp + "\ns=" + webappBuildVersion + " " + webappBuildDateStamp;
        return s;
    }
}
