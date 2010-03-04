package hotmath.gwt.shared.client.model;

import hotmath.gwt.shared.client.rpc.Response;

/** Central message coming from central server.
 * 
 *  Contains information sent from the server to a specific
 *  user.
 *  
 * @author casey
 *
 */
public class CentralMessage implements Response{
    
    Integer id;
    String message;
    String type;
    
    
    public CentralMessage(){}
    
    public CentralMessage(Integer id, String type,String msg) {
        this.id = id;
        this.type = type;
        this.message = msg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CentralMessage [id=" + id + ", message=" + message + ", type=" + type + "]";
    }
}
