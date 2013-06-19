package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
        
        dueDate.setInnerHTML(DateUtils4Gwt.getPrettyDateString(assignment.getAssignment().getDueDate()));
        
        List<String> statuses = new ArrayList<String>();
        if(assignment.getAssignment().isClosed()) {
            statuses.add("Closed");
        }
        if(assignment.isGraded()) {
            statuses.add("<span style='color: red'>Score: " + assignment.getHomeworkGrade() + "</span>");
            grade.setInnerHTML("");
            grade.setAttribute("style",  "display: none");
        }
        else if(assignment.isTurnedIn()) {
            statuses.add("Turned In");
            grade.setAttribute("style",  "display: none");
        }
        else {
            if(assignment.getAssignment().isExpired()) {
                statuses.add("Past Due");
            }
            else if(!assignment.getStatus().equals("Closed")) {
                statuses.add(assignment.getStatus());
            }
            grade.setAttribute("style",  "display: none");
        }
        
        String statusS="";
        for(String s: statuses) {
            if(statusS.length() > 0) {
                statusS += ", ";
            }
            statusS += s;
        }
        status.setInnerHTML(statusS);
    }
    
    @UiField
    HTMLPanel comments;
    @UiField
    Element dueDate, grade, status;
}
