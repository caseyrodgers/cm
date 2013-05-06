package hotmath.gwt.cm_mobile_shared.client.view;

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
            dueDate.setInnerHTML(getDueDateAsString(assignment.getAssignment().getDueDate()));
            grade.setAttribute("style",  "display: none");
        }
    }
    
    private String getDueDateAsString(Date dueDate) {
        
        long due = dueDate.getTime();
        int oneDay = (60*1000)*60*24;
        long now = new Date().getTime();
        
        String dueString;
        if(now > due) {
            dueString = "Overdue";
        }
        else {
            long today=now+oneDay;
            if( ((due-now)/oneDay) < 2) {
                dueString ="Today";
            }
            else if(due < now+(oneDay*2) ) {
                dueString = "Tomorrow";
            }
            else if(due < now+(oneDay*7)) {
                // day of week
                DateTimeFormat fmt = DateTimeFormat.getFormat("EEEE");
                dueString = fmt.format(dueDate);
            }
            else {
                dueString = dueDate.toString(); 
            }
        }
        return "Due: " + dueString;
    }

    @UiField
    HTMLPanel comments;
    @UiField
    Element dueDate, grade;
}
