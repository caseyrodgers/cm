package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.shared.client.rpc.Response;

/** Represents a single whiteboard action (draw,line,etc..)
 * and the related JSON data describing the action.
 * 
 * @author casey
 *
 */
public class WhiteboardCommand implements Response {
    
    String command;
    String data;
    
    public WhiteboardCommand() {}
    public WhiteboardCommand(String command, String data) {
        this.command = command;
        this.data = data;
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
