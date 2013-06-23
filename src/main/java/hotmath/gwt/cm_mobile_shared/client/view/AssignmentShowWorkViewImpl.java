package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.ProblemStatus;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentShowWorkViewImpl extends Composite implements AssignmentShowWorkView {

    private Presenter presenter;
    ShowWorkPanel2 _showWorkPanel;
    SexyButton _submitBtn;
    SubToolBar _subBar;
    
    public AssignmentShowWorkViewImpl() {
        _showWorkPanel = new ShowWorkPanel2(new ShowWorkPanelCallbackDefault() {
            @Override
            public void showWorkIsReady() {
                setPresenterAsync(presenter);
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return presenter.getWhiteboardSaveAction(pid, commandType, data);
            }
        },false);
        

        _subBar = new SubToolBar();
        _submitBtn = new SexyButton("Submit Whiteboard", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.submitShowWork();
                presenter.gotoTutorView();
            }
        });
        _subBar.add(_submitBtn);
        FlowPanel flow = new FlowPanel();
        flow.add(_subBar);

        flow.add(_showWorkPanel);
        _subBar.setVisible(false);
        initWidget(flow);
    }
    
    /** at this point we know the whiteboard is ready and waiting
     * 
     * @param presenter
     */
    private void setPresenterAsync(Presenter presenter) {
        presenter.prepareShowWorkView(this);
    }

    @Override
    public void setPresenter(Presenter listener) {
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
    public void loadWhiteboard(AssignmentWhiteboardData data,String problemStatement) {
        
        if(data.getStudentProblem().getProblem().getProblemType() == ProblemType.WHITEBOARD) {
            _subBar.setVisible(true);
            if(data.getStudentProblem().isGraded()) {
                _submitBtn.setEnabled(false);
            }
            else if(data.getStudentProblem().getStatus().equals(ProblemStatus.SUBMITTED.toString())) {
                _submitBtn.setEnabled(false);
            }
            else {
                _submitBtn.setEnabled(true);
            }
        }
        else {
            _subBar.setVisible(false);
        }
        
        _showWorkPanel.loadWhiteboard(data.getCommands());
        _showWorkPanel.setProblemStatement(problemStatement);
    }

    @Override
    public void isNowActive() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.ASSIGNMENT;        
    }

}
