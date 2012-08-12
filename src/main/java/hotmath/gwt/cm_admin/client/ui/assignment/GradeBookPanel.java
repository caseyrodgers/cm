package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentGradeBookAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GradeBookPanel extends ContentPanel {
    
    public GradeBookPanel(){
        setHeadingText("Selected Assignment Grade Book");
        
        getHeader().addTool(createAssignButton());
    }
    
    
    private Widget createAssignButton() {
        TextButton btn = new TextButton("Assign");
        btn.setToolTip("Assign students to the selected Assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                AssignStudentsToAssignmentDialog.getInstance().showDialog(_lastUsedAssignment);
            }
        });
        return btn;
    }
    
    Assignment _lastUsedAssignment;
    public void showGradeBookFor(Assignment assignment) {
        _lastUsedAssignment = assignment;
        readData(_lastUsedAssignment);
    }
    
    private void readData(final Assignment assignment) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                GetAssignmentGradeBookAction action = new GetAssignmentGradeBookAction(assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData responses) {
                Window.alert("Got Data: " + responses);
            }

        }.register();                
    }

}
