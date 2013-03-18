package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_tools.client.CatchupMathTools;

import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class StudentAssignmentButton extends IconButton {
    enum ButtonState{HAS_ASSIGNMENTS, NO_ASSIGNMENTS};
    ButtonState _state;
    public StudentAssignmentButton() {
        super("student-assignment-button-no");
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
    
    
    public void setState(AssignmentUserInfo assInfo) {
        if(assInfo.getActiveAssignments() > 0 || assInfo.getUnreadMessageCount() > 0) {
            _state = ButtonState.HAS_ASSIGNMENTS;
            
            changeStyle("student-assignment-button-new");
            
            int aa = assInfo.getActiveAssignments();
            int fa = assInfo.getUnreadMessageCount();
            String tip = "You have " + aa + " assignment" + (aa>1?"s":"") + ".  ";
            if(fa > 0) {
                tip += "You have " + fa + " new annotated problem" + (fa>1?"s":"") + ".";
            }
            setToolTip(tip);
        }
        else {
            _state = ButtonState.NO_ASSIGNMENTS;
            
            changeStyle("student-assignment-button-no");
            
            setToolTip("You do not have any assignments or annotations.");
        }
    }
}
