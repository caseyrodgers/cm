package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallbackDefault;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Widget;

public class ShowWorkViewImpl extends BaseComposite implements ShowWorkView {
    
    
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
                return presenter.getWhiteboardSaveAction(pid, commandType, data);
            }

            
        },false);
        initWidget(_showWorkPanel);
    }
    
    @Override
    public Place getBackPlace() {
        return null;
    }
    
    @Override
    public boolean useScrollPanel() {
        return false;
    }

    @Override
    public Widget asWidget() {
        return this;
    }
   
    
    @Override
    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
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

    private void setProblemStatement() {
       _showWorkPanel.setProblemStatement(presenter.getProblemStatementHtml());
    }

    @Override
    public void loadWhiteboard(CmList<WhiteboardCommand> commands) {
        _showWorkPanel.loadWhiteboard(commands);
    }    
    
}
