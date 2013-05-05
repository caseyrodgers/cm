package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import com.google.gwt.user.client.ui.IsWidget;

public interface AssignmentShowWorkView extends IPage, IsWidget {
    void setPresenter(Presenter listener);
    public interface Presenter {
        String getShowWorkTitle();
        void gotoTutorView();
        String getProblemStatementHtml();
        Action<? extends Response> getWhiteboardSaveAction(String pid, CommandType commandType, String commandData);
        void prepareShowWorkView(AssignmentShowWorkView showWorkView);
        void showWorkSubmitted();
        void submitShowWork();        
    }
    
    void loadWhiteboard(AssignmentWhiteboardData whiteData);
}
