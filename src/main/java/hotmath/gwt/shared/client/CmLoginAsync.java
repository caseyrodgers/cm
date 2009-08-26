package hotmath.gwt.shared.client;


/** Manages an async call to server to verify security key
 * 
 * @author casey
 *
 */
public interface CmLoginAsync {
    
    void loginSuccessful(Integer uid);

}
