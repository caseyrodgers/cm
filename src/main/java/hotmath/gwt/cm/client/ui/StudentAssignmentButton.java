package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentMetaInfo;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.util.MyResources;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class StudentAssignmentButton extends TextButton {
    enum ButtonState{HAS_ASSIGNMENTS, NO_ASSIGNMENTS};
    MyResources resources = GWT.create(MyResources.class);
    ButtonState _state;
    public StudentAssignmentButton() {
        setIcon(resources.assignmentBlue());
        _state = ButtonState.NO_ASSIGNMENTS;
        addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if (UserInfo.getInstance().isDemoUser()) {
                    CatchupMathTools.showAlert("Assignments are not available for demo accounts.");
                } else if(UserInfo.getInstance().isSingleUser()) {
                    CatchupMathTools.showAlert("Assignments are not available for single user accounts.");
                }
                else {
                    CatchupMath.getThisInstance().showAssignments_gwt();
                }
            }
        });
    }
    
    
    public void setState(AssignmentMetaInfo assInfo) {
        if(assInfo.getActiveAssignments() > 0 || assInfo.getUnreadMessages() > 0) {
            _state = ButtonState.HAS_ASSIGNMENTS;
            
            setIcon(resources.assignmentRed());

            int aa = assInfo.getActiveAssignments();
            int fa = assInfo.getUnreadMessages();
            String tip = "You have " + aa + " active assignment" + (aa>1?"s":"") + ".  ";
            if(fa > 0) {
                tip += "You have " + fa + " feedback message" + (fa>1?"s":"") + ".";
            }
            setToolTip(tip);
        }
        else {
            _state = ButtonState.NO_ASSIGNMENTS;
            setIcon(resources.assignmentBlue());
            setToolTip("You do not have any assignments or feedback.");
        }
    }
}
