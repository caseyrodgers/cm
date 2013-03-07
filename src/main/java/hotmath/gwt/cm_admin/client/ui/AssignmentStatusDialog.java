package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;

public class AssignmentStatusDialog extends GWindow {

    GradeBookPanel _gradingPanel;
    public AssignmentStatusDialog(Assignment asgn) {
        
        super(true);
        setPixelSize(500,  400);
        setHeadingText("Assignment Status: " + asgn.getDueDate() + " " + asgn.getComments());
        
        _gradingPanel = new GradeBookPanel();
        setWidget(_gradingPanel);
        _gradingPanel.showGradeBookFor(asgn);

        setVisible(true);
    }

}
