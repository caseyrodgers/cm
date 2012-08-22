package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentsContentPanel;
import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

/**
 * Provide dialog to allow Admins ability to define and manage Assignments.
 * 
 * 
 * @author casey
 * 
 */
public class AssignmentManagerDialog  {

    int aid;

    public AssignmentManagerDialog(int aid) {
        this.aid = aid;
        
        Window window = new GWindow(true);
        window.setPixelSize(800,600);
        window.setHeadingHtml("Assignment Manager");
        
        final BorderLayoutContainer con = new BorderLayoutContainer();
        con.setBorders(true);

        BorderLayoutData westData = new BorderLayoutData(350);
        westData.setCollapsible(false);
        westData.setSplit(true);
        westData.setCollapseMini(true);
        westData.setMargins(new Margins(0, 5, 0, 5));
        
        GradeBookPanel gradeBookPanel = new GradeBookPanel();
        ContentPanel assignmentPanel = new AssignmentsContentPanel(gradeBookPanel);
        
        assignmentPanel.setLayoutData(westData);
        
        con.setCenterWidget(gradeBookPanel);
        con.setWestWidget(assignmentPanel);
        
        window.setWidget(con);
        
        
        window.show();
    }
}

