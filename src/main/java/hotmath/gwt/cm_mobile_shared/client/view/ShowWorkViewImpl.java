package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallbackDefault;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class ShowWorkViewImpl extends AbstractPagePanel implements ShowWorkView {
    

    Presenter presenter;
    
    String title;
    ShowWorkPanel _showWorkPanel;
   
    public ShowWorkViewImpl() {
        _showWorkPanel = new ShowWorkPanel(new ShowWorkPanelCallbackDefault() {
            @Override
            public void showWorkIsReady() {
                setPresenterAsync(presenter);
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                Log.debug("Get Whiteboard action");
                return null;
            }
        },false);
        initWidget(_showWorkPanel);
    }

    
    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
        
        // showProblem.setValue(false);  // turn off showork as default
        
        /** make sure the whiteboard is initialized
         * 
         */
        Widget w = getParent();
        _showWorkPanel.setupWhiteboard();
    }

    /** at this point we know the whiteboard is ready and waiting
     * 
     * @param presenter
     */
    private void setPresenterAsync(Presenter presenter) {
        setProblemStatement();
        presenter.prepareShowWorkView(this);
    }

    @Override
    public String getTitle() {
        if(title != null) {
            return "Show Work for " + title;
        }
        else {
            return "Show Work";
        }
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
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
                return true;
            }
        };
    }
    
    @UiHandler("showProblem")
    protected void handleShowProblem(ClickEvent ce) {
        setProblemStatement();
    }
    

    private void setProblemStatement() {
       _showWorkPanel.setProblemStatement(presenter.getProblemStatementHtml());
    }
}