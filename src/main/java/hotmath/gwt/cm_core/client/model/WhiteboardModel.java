package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

/** Represents a single complete whiteboard
 * 
 * @author casey
 *
 */
public class WhiteboardModel implements Response {
    
    private List<WhiteboardCommand> commands;
    private String whiteboardId;

    public WhiteboardModel(){}
    
    public WhiteboardModel(String whiteboardId, List<WhiteboardCommand> commands) {
        this.commands = commands;
        this.whiteboardId = whiteboardId;
    }

    public List<WhiteboardCommand> getCommands() {
        return commands;
    }

    public void setCommands(List<WhiteboardCommand> commands) {
        this.commands = commands;
    }

    public String getWhiteboardId() {
        return whiteboardId;
    }

    public void setWhiteboardId(String whiteboardId) {
        this.whiteboardId = whiteboardId;
    }

}
