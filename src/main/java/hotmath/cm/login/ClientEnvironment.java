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
    boolean flashEnabled;
    
    public ClientEnvironment() {
        this.userAgent="";
        this.flashEnabled = true;
    }
    
    public ClientEnvironment(boolean supportsFlash) {
        this();
        this.flashEnabled = supportsFlash;
    }
    
    public ClientEnvironment(String userAgent) {
        this();
        this.userAgent = userAgent;
    }
    
    public ClientEnvironment(String userAgent, boolean supportsFlash) {
        this.userAgent = userAgent;
        this.flashEnabled = supportsFlash;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isFlashEnabled() {
        return false;
        //return flashEnabled;
    }

    public void setFlashEnabled(boolean flashEnabled) {
        this.flashEnabled = flashEnabled;
    }

    @Override
    public String toString() {
        return "ClientEnvironment [userAgent=" + userAgent + ", flashEnabled=" + flashEnabled + "]";
    }
}
