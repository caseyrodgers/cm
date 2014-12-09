package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AssignmentStatusDialog extends GWindow {

    GradeBookPanel _gradingPanel;
    BorderLayoutContainer _container;

    public AssignmentStatusDialog(final Assignment asgn) {
        
        super(false);
        setPixelSize(500, 450);
        setMaximizable(true);
        setHeadingText("Student Status");

        _container = new BorderLayoutContainer();
        _container.setBorders(true);

        BorderLayoutData centerData = new BorderLayoutData();
        centerData.setSize(400);

        displaySummary("Due: " + asgn.getDueDate() + " " + asgn.getComments());

        _gradingPanel = new GradeBookPanel();
        _container.setCenterWidget(_gradingPanel, centerData);
        _gradingPanel.showGradeBookFor(asgn);

        setWidget(_container);

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
        if(asgn.getStatus().equals("Draft") || CmShared.isDebug() == true) {
            addRemove.setEnabled(true);
        } else {
            addRemove.setEnabled(false);
        }
        addButton(addRemove);
        addCloseButton();
        

        setVisible(true);
    }

    private void displaySummary(String summary) {
    	FlowLayoutContainer flc = new FlowLayoutContainer();
    	flc.setScrollMode(ScrollMode.AUTO);
    	flc.setBorders(false);

    	BorderLayoutData bld = new BorderLayoutData(50);
    	bld.setMargins(new Margins(5));

        flc.add(new HTML("<p style='padding: 5px;'>" + summary + "</p>"));

        _container.setNorthWidget(flc, bld);
	}

    public static void startTest() {
        //new AssignmentManagerDialog2(566,2);
        //return;
        Assignment ass = new Assignment();
        ass.setAdminId(2);
        ass.setAssignKey(23);
        ass.setStatus("Open");
        new AssignmentStatusDialog(ass);
    }

    
}
