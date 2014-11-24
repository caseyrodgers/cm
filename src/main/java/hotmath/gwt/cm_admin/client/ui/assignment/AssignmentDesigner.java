package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tutor.client.event.TutorPanelExclusiveEvent;
import hotmath.gwt.cm_tutor.client.event.TutorPanelExclusiveHandler;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

/** Provide ability to design/create/modify an Assignment
 * 
 * @author casey
 *
 */
public class AssignmentDesigner extends BorderLayoutContainer {
    
    Assignment _assignment;
    
    public interface AssignmentDesignerCallback {
        boolean isDraftMode();
    }
    AssignmentDesignerCallback _callBack;
    
    public AssignmentDesigner(Assignment assignment, AssignmentDesignerCallback callBack) {
        this._assignment = assignment;
        this._callBack = callBack;
        createUi();
        readAssignmentProblems();
        
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventType() == EventType.EVENT_TYPE_QUESTION_VIEWER_CLOSED) {
                    setupViewerGui();
                }
            }
        });
        
        CmRpcCore.EVENT_BUS.addHandler(TutorPanelExclusiveEvent.TYPE, new TutorPanelExclusiveHandler() {
            @Override
            public void tutorNeedsExclusiveAccess() {
                QuestionViewerPanel.getInstance().removeQuestion();
            }
        });

    }   

    BorderLayoutData _viewerData;
    BorderLayoutContainer _mainContainer;
    private void createUi() {
        
        BorderLayoutData ld = new BorderLayoutData();
        setCenterWidget(createProblemList(), ld);
        
        setupViewerGui();
    }
    
    private void setupViewerGui() {
        _viewerData = new BorderLayoutData();
        _viewerData.setCollapsible(true);
        _viewerData.setFloatable(true);
        _viewerData.setSize(.5);
        //_viewerData.setHidden(true);
        setEastWidget(QuestionViewerPanel.getInstance(), _viewerData);
        
        forceLayout();
    }
    
    AssignmentProblemListView _listView;

    private Widget createProblemList() {
        _listView = new AssignmentProblemListView(_assignment, new Callback() {
            @Override
            public void problemHasBeenSelected(ProblemDto problem) {
                _viewerData.setHidden(false);
                QuestionViewerPanel.getInstance().viewQuestion(problem, false);
                forceLayout();
            }
            
            @Override
            public boolean isDraftMode() {
                return _callBack.isDraftMode();
            }
            
            @Override
            public void clearTutorView() {
                QuestionViewerPanel.getInstance().removeQuestion();
            }
        });
        BorderLayoutData data = new BorderLayoutData();
        data.setSize(450.0);
        _listView.setLayoutData(data);
        return _listView;
    }

    public boolean getIsModified() {
    	return _listView.getIsModified();
    }

    public void setIsModified(boolean isModified) {
    	_listView.setIsModified(isModified);
    }

    static interface Callback {
        void problemHasBeenSelected(ProblemDto problem);
        boolean isDraftMode();
        void clearTutorView();
    }
    
    public List<ProblemDto> getAssignmentPids() {
        List<ProblemDto> data = _listView.getAssignmentPids();
        return data;
    }
    
    
    private void readAssignmentProblems() {
        if(_assignment.getAssignKey() == 0) {
            return;
        }
        
        
        QuestionViewerPanel.getInstance().removeQuestion();
        
        new RetryAction<Assignment>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAssignmentAction action = new GetAssignmentAction(_assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(Assignment assignment) {
                CmBusyManager.setBusy(false);
                
                
                if(assignment.getPids().size() == 0) {
                    _listView.setNoProblemsMessge();    
                }
                else {
                    _listView.setProblemListing(assignment.getPids());
                }
            }
            
            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);
                super.onFailure(error);
            }

        }.register();
    }
}



