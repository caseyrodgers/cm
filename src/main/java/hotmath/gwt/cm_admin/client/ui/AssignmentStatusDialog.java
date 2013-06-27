package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AssignmentStatusDialog extends GWindow {

    GradeBookPanel _gradingPanel;
    public AssignmentStatusDialog(final Assignment asgn) {
        
        super(false);
        setPixelSize(500,  400);
        setMaximizable(true);
        setHeadingText("Assignment Status: Due: " + asgn.getDueDate() + " " + asgn.getComments());
        
        _gradingPanel = new GradeBookPanel();
        setWidget(_gradingPanel);
        _gradingPanel.showGradeBookFor(asgn);
        

        addButton(new TextButton("Add/Remove Students", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new AssignmentAddRemoveStudents(asgn);
            }
        }));
        addCloseButton();
        

        setVisible(true);
    }
    
    public static void startTest() {
        //new AssignmentManagerDialog2(566,2);
        //return;
        Assignment ass = new Assignment();
        ass.setAdminId(2);
        ass.setAssignKey(23);
        new AssignmentStatusDialog(ass);
    }

    
}
