package hotmath.gwt.cm_mobile_shared.client.view;


import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.SexyButton;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
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

    GenericContainerTag listItems = new GenericContainerTag("ul");
    SexyButton _turnInAssignment;

    private StudentAssignment _lastAssignment;

    public AssignmentViewImpl() {

        FlowPanel flow = new FlowPanel();
        SubToolBar subBar = new SubToolBar();
        _turnInAssignment = new SexyButton("Turn In Assignment", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.turnInAssignment(AssignmentViewImpl.this);
            }
        });
        subBar.add(_turnInAssignment);
        subBar.add(new SexyButton("Refresh", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                presenter.reloadAssignment(AssignmentViewImpl.this);
            }
        }));
        
        flow.add(subBar);

        _main = new FlowPanel();
        _main.getElement().setAttribute("style",  "margin: 10px;");

        _main.add(new HTML("Loading Assigment ..."));
        flow.add(_main);
        initWidget(flow);

        listItems.addStyleName("touch");
        listItems.addStyleName("large");

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
        listItems.clear();
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
            listItems.add(tt);
        }
        
        _turnInAssignment.setEnabled(!assignment.isTurnedIn());
        _main.clear();
        _main.add(listItems);
    }

    class MyGenericTextTag extends GenericTextTag<String> {
        private StudentProblemDto _problem;

        public MyGenericTextTag(StudentProblemDto problem) {
            super("li", problem.getStudentLabelWithStatus());
            this._problem = problem;
        }
    }
}
