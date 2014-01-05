package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WhiteboardModel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.SaveStaticWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;

public class ProblemDesignerEditorWhiteboard extends GWindow {
    
    private String whiteboardId;
    private SolutionInfo solution;
    private SimpleContainer _main;
    private CallbackOnComplete callback;
    private int _countChanges;

    public ProblemDesignerEditorWhiteboard(SolutionInfo solution, String whiteboardId, CallbackOnComplete callbackIn) {
        super(true);
        this.callback = callbackIn;
        this.solution = solution;
        this.whiteboardId = whiteboardId;
        setPixelSize(800,  600);
        
        setHeadingText("Edit Problem Definition: " + solution.getPid());
        _main = new SimpleContainer();
        setWidget(_main);
        setVisible(true);
        buildUi();
        
        addBeforeHideHandler(new BeforeHideHandler() {
            @Override
            public void onBeforeHide(BeforeHideEvent event) {
                if(_countChanges > 0) {
                    callback.isComplete();
                }
            }
        });
    }
    
    private void buildUi() {
        final ShowWorkPanel2Callback callBack = new ShowWorkPanel2Callback() {
            @Override
            public void windowResized() {
            }
            
            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
                WhiteboardModel whiteBoard = solution.getWhiteboards().size() > 0?solution.getWhiteboards().get(0):new WhiteboardModel();
                showWork.loadWhiteboard(whiteBoard.getCommands());
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                _countChanges++;
                return new SaveStaticWhiteboardDataAction(UserInfoBase.getInstance().getUid(),solution.getPid(), commandType, data);
            }
        };
        _main.setWidget(new ShowWorkPanel2(callBack,true,true,"wb_ps-1",545, getWidget()));
    }

}
