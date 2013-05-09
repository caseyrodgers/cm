package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AssignmentHeaderPanel extends Composite {
    
    interface MyUiBinder extends UiBinder<Widget, AssignmentHeaderPanel> { }
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    public AssignmentHeaderPanel() {
       initWidget(uiBinder.createAndBindUi(this));
    }

    public void loadAssignment(StudentAssignment assignment) {
        comments.getElement().setInnerHTML(assignment.getAssignment().getComments());
        
        if(assignment.isGraded()) {
            dueDate.setInnerHTML("Status: Graded");
            grade.setInnerHTML("Grade: " + assignment.getHomeworkGrade());
            grade.setAttribute("style",  "display: block");
        }
        else if(assignment.isTurnedIn()) {
            dueDate.setInnerHTML("Turned In");
            grade.setAttribute("style",  "display: none");
        }
        else {
            dueDate.setInnerHTML(DateUtils4Gwt.getPrettyDateString(assignment.getAssignment().getDueDate()));
            grade.setAttribute("style",  "display: none");
        }
    }
    
    @UiField
    HTMLPanel comments;
    @UiField
    Element dueDate, grade;
}
