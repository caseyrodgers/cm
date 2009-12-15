package hotmath.gwt.shared.client.rpc.result;

import java.io.Serializable;

/** Represents a single entry in an auto registration setup
 * 
 * @author casey
 *
 */
public class AutoRegistrationEntry implements Serializable {

    String name;
    String password;
    String message;
    boolean isError;
    
    

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
