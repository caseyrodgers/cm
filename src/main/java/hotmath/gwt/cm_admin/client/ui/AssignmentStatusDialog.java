package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookPanel;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.ReleaseAssignmentGradesAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.info.Info;

public class AssignmentStatusDialog extends GWindow {

    GradeBookPanel _gradingPanel;
    private Assignment _assigment;
    public AssignmentStatusDialog(final Assignment asgn) {
        
        super(false);
        this._assigment = asgn;
        
        setPixelSize(500,  400);
        setHeadingText("Assignment Status: Due: " + asgn.getDueDate() + " " + asgn.getComments());
        
        _gradingPanel = new GradeBookPanel();
        setWidget(_gradingPanel);
        _gradingPanel.showGradeBookFor(asgn);
        
        addButton(new TextButton("Report Grades", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                markSelectedAssignmentAsGraded(asgn);
            }
        }));
        
        addCloseButton();
        

        setVisible(true);
    }

    protected void markSelectedAssignmentAsGraded(final Assignment assignment) {
        final ConfirmMessageBox cm = new ConfirmMessageBox("Report Grades", "Are you sure you want to allow students to see this assignment's grades?");
        cm.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                    releaseGradesForAssignment(assignment);
                }
            }
        });
        cm.setVisible(true);
    }

    protected void releaseGradesForAssignment(final Assignment assignment) {
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                ReleaseAssignmentGradesAction action = new ReleaseAssignmentGradesAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData saList) {
                Info.display("Infomation", "Grades Reported");
                CmBusyManager.setBusy(false);
                _gradingPanel.showGradeBookFor(_assigment);
                CmRpc.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent());
            }
        }.register();                        
    }

    
}
