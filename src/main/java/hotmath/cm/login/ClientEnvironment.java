package hotmath.cm.login;

/** Class to encapsulate the users browser environment
 * 
 * default is to support Flash
 * 
 * @author casey
 *
 */
public class ClientEnvironment {
    String userAgent;
    boolean supportsFlash;
    
    public ClientEnvironment() {
        this.userAgent="";
        this.supportsFlash = true;
    }
    
    public ClientEnvironment(boolean supportsFlash) {
        this();
        this.supportsFlash = supportsFlash;
    }
    
    public ClientEnvironment(String userAgent) {
        this();
        this.userAgent = userAgent;
    }
    
    public ClientEnvironment(String userAgent, boolean supportsFlash) {
        this.userAgent = userAgent;
        this.supportsFlash = supportsFlash;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isSupportsFlash() {
        return supportsFlash;
    }

    public void setSupportsFlash(boolean supportsFlash) {
        this.supportsFlash = supportsFlash;
    }

    @Override
    public String toString() {
        return "ClientEnvironment [userAgent=" + userAgent + ", supportsFlash=" + supportsFlash + "]";
    }
}
