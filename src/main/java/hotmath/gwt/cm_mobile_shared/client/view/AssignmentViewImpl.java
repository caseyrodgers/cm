package hotmath.gwt.cm_mobile_shared.client.view;


import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox.CallBack;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.event.UpdateAssignmentViewEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.UpdateAssignmentViewHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class AssignmentViewImpl extends Composite implements AssignmentView {

    private Presenter presenter;

    FlowPanel _main;

    GenericList list = new GenericList();
    SexyButton _turnInAssignment;
    AssignmentHeaderPanel _headerInfo;

    private StudentAssignment _lastAssignment;

    private CallbackOnComplete callbackWhenLoaded;

    public AssignmentViewImpl() {

        FlowPanel flow = new FlowPanel();
        SubToolBar subBar = new SubToolBar(true);
        _turnInAssignment = new SexyButton("Turn In Assignment", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                QuestionBox.askYesNoQuestion("Turn In Assignment",  "Are you sure you want to turn in this assignment?", new CallBack() {
                    @Override
                    public void onSelectYes() {
                        presenter.turnInAssignment(AssignmentViewImpl.this);
                        _turnInAssignment.setEnabled(false);
                    }
                });
            }
        });
        subBar.add(_turnInAssignment);
        subBar.add(new SexyButton("Check For Changes", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                presenter.refreshAssignment(AssignmentViewImpl.this);
            }
        }));
        
        flow.add(subBar);
        _headerInfo = new AssignmentHeaderPanel();
        flow.add(_headerInfo);

        _main = new FlowPanel();
        _main.getElement().setAttribute("style",  "margin: 10px;");

        _main.add(new HTML("Loading Assigment ..."));
        flow.add(_main);
        initWidget(flow);

        addStyleName("AssignmentViewImpl");

    }

    @Override
    public String getViewTitle() {
        return"Assignment: " + DateUtils4Gwt.getPrettyDateString(_lastAssignment.getAssignment().getDueDate(), true);
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
                presenter.gotoAssignmentList();
                return false;
            }
        };
    }

    @Override
    public void setPresenter(Presenter presenter, CallbackOnComplete callback) {
        this.presenter = presenter;
        this.callbackWhenLoaded = callback;
        
        presenter.loadAssignment(this);
    }

    @Override
    public void loadAssignment(StudentAssignment assignment) {
        _lastAssignment = assignment;
        _main.setVisible(false);
        list.getList().clear();
        for (StudentProblemDto problem : assignment.getStudentStatuses().getAssigmentStatuses()) {
            GenericTextTag<String> tt = new MyGenericTextTag(problem);
            tt.addStyleName("group");
            tt.addHandler(new TouchClickHandler<String>() {
                @Override
                public void touchClick(TouchClickEvent<String> event) {
                    MyGenericTextTag tag = (MyGenericTextTag) event.getTarget();
                    presenter.showProblem(tag._problem);
                }
            });
            list.getList().add(tt);
        }
        list.updateCount();
        _turnInAssignment.setEnabled(!assignment.getAssignment().isClosed() && !assignment.isGraded() && !assignment.isTurnedIn());
        _main.clear();
        _main.add(list);
        
        _headerInfo.loadAssignment(assignment);
        _main.setVisible(true);
        
        
        callbackWhenLoaded.isComplete();
    }

    class MyGenericTextTag extends GenericTextTag<String> {
        private StudentProblemDto _problem;

        public MyGenericTextTag(StudentProblemDto problem) {
            super("li");
            
            getElement().setAttribute("style", "position: relative");
            getElement().setAttribute("pid", problem.getPid());
            
            this._problem = problem;
            
            
            String label = problem.getStudentLabel();
            String status = problem.getStatusForStudent();
            
            if(status.equals("Incorrect")) {
                //status = "<img style='float: right;' src='/gwt-resources/images/mobile/check-mark-3-24.png'/>";
                status = "<img style='float: right;' src='/gwt-resources/images/mobile/x-mark-24.png'/>";
                
            }
            else if(status.equals("Correct")) {
                status = "<img style='float: right;' src='/gwt-resources/images/mobile/check-mark-3-24.png'/>";
            }
            else if(status.equals("Not Answered")) {
                status = "<img style='float: right;' src='/gwt-resources/images/mobile/not_answered-24.png'/>";
            }
            else if(status.equals("Half Credit")) {
                status = "<img style='float: right;' src='/gwt-resources/images/mobile/one_half-24.png'/>";
            }
            else if(status.equals("Submitted")) {
                status = "<img style='float: right;' src='/gwt-resources/images/mobile/problem_submitted-24.png'/>";
            }


            String properties = "";
            if(problem.isHasShowWorkAdmin()) {
                if(AssignmentData.doesPidHaveTeacherNote(_lastAssignment.getAssignment().getAssignKey(), problem.getPid())) {
                      properties = "<img style='float: right;' src='/gwt-resources/images/mobile/has_notes_unread.gif'/>";
                  }
                  else {
                      properties = "<img style='float: right;' src='/gwt-resources/images/mobile/has_notes.png'/>";
                  }
              }

            
            String html=label + "<div style='float: right;margin-right: 15px;font-size: .8em;color: gray'>" + status + "</div>";
            if(problem.isComplete()) {
                html = "<span style='color: gray'>" + html + "</html>";
            }

            html = html + properties;

//            String html = problem.getStudentLabelWithStatus();
//            
//            
//            if(problem.isHasShowWorkAdmin()) {
//                if(AssignmentData.doesPidHaveTeacherNote(_lastAssignment.getAssignment().getAssignKey(), problem.getPid())) {
//                    html = "<img style='position: absolute;top:0;left:0;' src='/gwt-resources/images/mobile/has_notes_unread.png'/>" + html;
//                }
//                else {
//                    html = "<img style='position: absolute;top:0;left:0;' src='/gwt-resources/images/mobile/has_notes.png'/>" + html;
//                }
//            }
            setHtml(html);
        }
    }

    @Override
    public void isNowActive() {
        int SIZE_OF_HEADER=500;
        int lastScrollPosition = CatchupMathMobile3.__instance.getScrollPositionFor(this);
        if(lastScrollPosition > 0) {
            jsni_scrollToPosition(lastScrollPosition + SIZE_OF_HEADER);
        }
    }
    

    native void jsni_scrollToPosition(int lastScrollPosition) /*-{
         $wnd.scrollTo(0,  lastScrollPosition);
    }-*/;

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.ASSIGNMENT;        
    }
    
}


