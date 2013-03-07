package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.UserInfo;
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
    
    
    public void setState(int activeAssignments, int unreadFeedback) {
        if(activeAssignments > 0 || unreadFeedback > 0) {
            _state = ButtonState.HAS_ASSIGNMENTS;
            setIcon(resources.assignmentRed());
            setToolTip("You have assignments: " + " active: " + activeAssignments + ", new feedback: " + unreadFeedback);
        }
        else {
            _state = ButtonState.NO_ASSIGNMENTS;
            setIcon(resources.assignmentBlue());
            setToolTip("You do not have any assignments or feedback.");
        }
    }
}
