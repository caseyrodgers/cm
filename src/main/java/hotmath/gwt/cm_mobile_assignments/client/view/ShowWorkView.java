package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;


public interface ShowWorkView extends BaseView{
    
    void setPresenter(Presenter presenter);
    public interface Presenter {
        void prepareShowWorkView(ShowWorkView showWorkView);
        String getProblemStatementHtml();
        Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String commandData);
    }
    
    /** Load the whiteboard commands 
     * 
     *  Called by activity once data has been read from server
     * 
     * @param commands
     */
    void loadWhiteboard(CmList<WhiteboardCommand> commands);
}
