package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;

public class AssignmentStatusDialog extends GWindow {

    GradeBookPanel _gradingPanel;
    public AssignmentStatusDialog(final Assignment asgn) {
        
        super(false);
        setPixelSize(500,  400);
        setHeadingText("Assignment Status: Due: " + asgn.getDueDate() + " " + asgn.getComments());
        
        _gradingPanel = new GradeBookPanel();
        setWidget(_gradingPanel);
        _gradingPanel.showGradeBookFor(asgn);
        
        addCloseButton();
        

        setVisible(true);
    }

    
}
