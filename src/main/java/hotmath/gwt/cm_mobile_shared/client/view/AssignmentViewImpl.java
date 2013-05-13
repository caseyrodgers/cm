package hotmath.gwt.cm_mobile_shared.client.view;


import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;

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

    public AssignmentViewImpl() {

        FlowPanel flow = new FlowPanel();
        SubToolBar subBar = new SubToolBar();
        _turnInAssignment = new SexyButton("Turn In Assignment", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.turnInAssignment(AssignmentViewImpl.this);
                presenter.gotoAssignmentList();
            }
        });
        subBar.add(_turnInAssignment);
        subBar.add(new SexyButton("Refresh", new ClickHandler() {
            
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
        return "Assignment View";
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
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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
        _turnInAssignment.setEnabled(!assignment.isGraded() && !assignment.isTurnedIn());
        _main.clear();
        _main.add(list);
        
        _headerInfo.loadAssignment(assignment);
        _main.setVisible(true);
    }

    class MyGenericTextTag extends GenericTextTag<String> {
        private StudentProblemDto _problem;

        public MyGenericTextTag(StudentProblemDto problem) {
            super("li");
            
            this._problem = problem;

            String html = problem.getStudentLabelWithStatus();
            getElement().setAttribute("style", "position: relative");
            
            if(problem.isHasShowWorkAdmin()) {
                if(AssignmentData.doesPidHaveTeacherNote(_lastAssignment.getAssignment().getAssignKey(), problem.getPid())) {
                    html = "<img style='position: absolute;top:0;left:0;' src='/gwt-resources/images/assignments/has_notes_unread.png'/>" + html;
                }
                else {
                    html = "<img style='position: absolute;top:0;left:0;' src='/gwt-resources/images/assignments/has_notes.png'/>" + html;
                }
            }
            setHtml(html);
        }
    }
}


