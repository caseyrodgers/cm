package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;


/** Represents a single whiteboard action (draw,line,etc..)
 * and the related JSON data describing the action.
 * 
 * @author casey
 *
 */
public class WhiteboardCommand implements Response {
    
    boolean isAdmin;
    String command;
    String data;
    
    public WhiteboardCommand() {}
    public WhiteboardCommand(String command, String data, boolean isAdmin) {
        this.command = command;
        this.data = data;
        this.isAdmin = isAdmin;
    }

    
    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
