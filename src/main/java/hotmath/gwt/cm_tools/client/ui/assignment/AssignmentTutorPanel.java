package hotmath.gwt.cm_tools.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.assignment.event.AssignmentProblemLoadedEvent;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;


/** Provide Generic access to a single solution in an Assignment
 * 
 * @author casey
 *
 */
public class AssignmentTutorPanel extends Composite {
    
    static AssignmentTutorPanel __lastInstance;

    TutorWrapperPanel _tutorPanel;

    AssignmentTutorPanelCallback _callBack;
    boolean _isEditable;

    private boolean _isGraded;

    interface AssignmentTutorPanelCallback {
        void tutorWidgetValueUpdated(String value, boolean correct);

        void whiteboardSubmitted();
    }
    public AssignmentTutorPanel(final boolean isEditable, final ProblemDto problemDto, final boolean isGraded, AssignmentTutorPanelCallback callBack) {
        
        setupJsni();
        
        _callBack = callBack;
        __lastInstance = this;
        _isEditable = isEditable;
        _isGraded = isGraded;
        boolean showButtonBar = !isEditable;
        _tutorPanel = new TutorWrapperPanel(showButtonBar,false,false, true, new TutorCallbackDefault() {
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                processTutorWidgetComplete(inputValue, correct);
            }
            
            @Override
            public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid,
                    int problemNumber) {
                return new SaveAssignmentSolutionContextAction(_assProblem.getUserId(), _assignKey, _assProblem.getInfo().getPid(), 0, variablesJson);
            }
            
            @Override
            public void tutorWidgetCompleteDenied(String inputValue, boolean correct) {
                CmMessageBox.showAlert("Changes are not allowed.");
            }
            
            @Override
            public boolean moveFirstHintOnWidgetIncorrect() {
                return !isEditable;
            }
            
            @Override
            public String getSubmitButtonText() {
                return "Submit Answer";
            }
            
            @Override
            public WidgetStatusIndication indicateWidgetStatus() {
                return !isGraded?WidgetStatusIndication.INDICATE_SUBMIT_ONLY:WidgetStatusIndication.DEFAULT;
            }
            
            @Override
            public boolean installCustomSteps() {
                return problemDto.getProblemType() == ProblemType.MULTI_CHOICE;
            }
            
        });
        
//        if(!isEditable) {
//            _tutorPanel.setReadOnly(true);
//        }
        
        _tutorPanel.addStyleName("tutor_solution_wrapper");
        isEpp = true;
        _tutorPanel.addStyleName("is_epp");
        
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);
        flow.add(_tutorPanel);

        initWidget(flow);
    }
    
    

    SolutionInfo solutionInfo;

    boolean isEpp;
    
    public AssignmentProblem getAssignmentProblem() {
        return _assProblem;
    }

    AssignmentProblem _assProblem;
    public void loadSolution(final int uid, final int assignKey, final StudentProblemDto stuProb) {

        GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(uid,assignKey, stuProb.getPid());
        CmTutor.getCmService().execute(action, new AsyncCallback<AssignmentProblem>() {
            @Override
            public void onSuccess(AssignmentProblem problem) {
                loadSolutionIntoGui(uid, assignKey, problem, stuProb);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error loading solution: " + stuProb.getPid(), caught);
                CmMessageBox.showAlert("Error loading solution", "There was a problem talking to the server");
            }
        });
    }

    int _assignKey;
    int _uid;
    private void loadSolutionIntoGui(int uid, int assignKey, AssignmentProblem problem, StudentProblemDto stuProb) {
        try {
            _uid = uid;
            _assProblem = problem;
            _assignKey = assignKey;
            String resourceTitle = "Problem: " + problem.getInfo().getPid();

            String variableContext = null;

             if(problem.getInfo().getContext() != null) {
                 variableContext = problem.getInfo().getContext().getContextJson();
             }

            InmhItemData item = new InmhItemData("solution", problem.getInfo().getPid(), resourceTitle);

            _tutorPanel.externallyLoadedTutor(problem.getInfo(),getWidget(), item.getFile(), item.getWidgetJsonArgs(), problem.getInfo().getJs(),problem.getInfo().getHtml(), resourceTitle, true, false, variableContext);
            
            
            if(problem.getLastUserWidgetValue() != null) {
                _tutorPanel.setTutorWidgetValue(problem.getLastUserWidgetValue());
            }
            
            if(problem.getProblemType() == ProblemType.WHITEBOARD) {
                String status = stuProb.getStatus();
                
                if(!status.equals("Submitted")) {
                    addWhiteboardSubmitButton();
                }
                else {
                    jsni_showWhiteboardStatus(status);
                }
            }
            
            
            _tutorPanel.setVisible(true);

            CmRpc.EVENT_BUS.fireEvent(new AssignmentProblemLoadedEvent(problem));
            
        } catch (Exception e) {
            Log.error("Error loading solution into GUI", e);
        }

    }
    
    native private void jsni_showWhiteboardStatus(String status) /*-{
        if(status == 'Submitted' || status == 'Correct' || status == 'Incorrect' || status == 'Half Correct') {
            var widgetHolder = $doc.getElementById("hm_flash_widget");
            widgetHolder.innerHTML = "<div id='hm_flash_widget_head' style='display: block'>" + status + "</div>";
            //$wnd.setWidgetMessage(status);
        }
    }-*/;

    private void gwt_submitWhiteboardAnswer() {
        jsni_showWhiteboardStatus("Submitted");
        _callBack.whiteboardSubmitted();
    }

    native private void setupJsni() /*-{
        var that = this;
        $wnd.gwt_submitWhiteboardAnswer = function() {
            that.@hotmath.gwt.cm_tools.client.ui.assignment.AssignmentTutorPanel::gwt_submitWhiteboardAnswer()();
        }
    }-*/;


    private native void addWhiteboardSubmitButton() /*-{
        var widgetHolder = $doc.getElementById("hm_flash_widget");
        if(widgetHolder) {
            var ih = widgetHolder.innerHTML;
            widgetHolder.innerHTML = ih + "<p><input type='button' onclick='gwt_submitWhiteboardAnswer()' class='sexybutton sexysimple sexylarge sexyred' value='Submit Whiteboard Answer'/></p>";
        }
    }-*/;

    /** Save user's input widget value to server
     * 
     * @param inputValue
     * @param yesNo
     */
    private void processTutorWidgetComplete(final String inputValue, boolean yesNo) {

        
        if(_isGraded) {
            CmMessageBox.showAlert("Assignment Already Graded", "This input value will not be saved because the assignment has already been graded.");
            return;
        }

        if(!_isEditable) {
            CmMessageBox.showAlert("Assignment Closed", "This input value will not be saved because the assignment is closed.");
            return;
        }

        
        SaveAssignmentTutorInputWidgetAnswerAction action = new SaveAssignmentTutorInputWidgetAnswerAction(_uid, _assignKey,_assProblem.getInfo().getPid(),inputValue,yesNo);
        CmTutor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                _assProblem.setLastUserWidgetValue(inputValue);
                //loadSolutionIntoGui(_uid, _assignKey, _assProblem);  // to show readonly state
                Log.debug("Tutor Widget Answer saved to server.");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error saving tutor widget input value.",caught);
            }
        });
        
        _callBack.tutorWidgetValueUpdated(inputValue,  yesNo);
    }

}

