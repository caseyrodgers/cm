package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AssignmentStatusDialog extends GWindow {

    GradeBookPanel _gradingPanel;
    public AssignmentStatusDialog(final Assignment asgn) {
        
        super(false);
        setPixelSize(500,  400);
        setMaximizable(true);
        setHeadingText("Student Status: Due: " + asgn.getDueDate() + " " + asgn.getComments());
        
        _gradingPanel = new GradeBookPanel();
        setWidget(_gradingPanel);
        _gradingPanel.showGradeBookFor(asgn);
        

        TextButton addRemove = new TextButton("Add/Remove Students", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                
                if(UserInfoBase.getInstance().isMobile()) {
                    new FeatureNotAvailableToMobile();
                    return;
                }
                
                new AssignmentAddRemoveStudents(asgn, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        _gradingPanel.showGradeBookFor(asgn);
                    }
                });
            }
        });
        addRemove.setToolTip("Add or remove students assigned to this assignment (Draft mode only.)");
        if(asgn.getStatus().equals("Draft") || CmShared.getQueryParameter("debug") != null) {
            addRemove.setEnabled(true);
        } else {
            addRemove.setEnabled(false);
        }
        addButton(addRemove);
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
