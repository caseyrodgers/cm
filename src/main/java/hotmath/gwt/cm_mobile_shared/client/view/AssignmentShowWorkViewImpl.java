package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallbackDefault;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentShowWorkViewImpl extends Composite implements AssignmentShowWorkView {

    private Presenter presenter;
    ShowWorkPanel _showWorkPanel;
    FlowPanel _main = new FlowPanel();
    
    public AssignmentShowWorkViewImpl() {
        _showWorkPanel = new ShowWorkPanel(new ShowWorkPanelCallbackDefault() {
            @Override
            public void showWorkIsReady() {
                setPresenterAsync(presenter);
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return presenter.getWhiteboardSaveAction(pid, commandType, data);
            }
        },false);
        _main.add(new HTML("Whiteboard loading ..."));
        initWidget(_main);
    }
    
    /** at this point we know the whiteboard is ready and waiting
     * 
     * @param presenter
     */
    private void setPresenterAsync(Presenter presenter) {
        _showWorkPanel.setProblemStatement(presenter.getProblemStatementHtml());
        presenter.prepareShowWorkView(this);
    }

    @Override
    public void setPresenter(Presenter listener) {
        _main.clear();
        
        SubToolBar sub = new SubToolBar();
        sub.add(new SexyButton("Submit Answer", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.showWorkSubmitted();
            }
        }));
        _main.add(sub);
        _main.add(_showWorkPanel);
        this.presenter = listener;
        _showWorkPanel.setupWhiteboard();
    }
    
    @Override
    public String getViewTitle() {
        return presenter.getShowWorkTitle();
    }

    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                presenter.gotoTutorView();
                return false;
            }
        };
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void loadWhiteboard(CmList<WhiteboardCommand> commands) {
        _showWorkPanel.loadWhiteboard(commands);
    }

}