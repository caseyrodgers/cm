package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemAnnotation;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import java.util.List;

import com.google.gwt.user.client.Timer;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GotoNextAnnotationButton extends TextButton implements SelectHandler {
    private static int next;
    public GotoNextAnnotationButton() {
        super("Goto Teacher Note");
        addSelectHandler(this);
        
        setToolTip("Show the next unread teacher notation");
        setEnabled(false);
        startChecking();
    }
    
    private void checkIt() {
        if(UserInfo.getInstance().getAssignmentMetaInfo() != null && UserInfo.getInstance().getAssignmentMetaInfo().getUnreadAnnotations().size() > 0) {
            setEnabled(true);
        }
        else {
            setEnabled(false);
        }
    }
    private void startChecking() {
        new Timer() {
            @Override
            public void run() {
                checkIt();
            }
        }.scheduleRepeating(10000);
        checkIt();
    }

    @Override
    public void onSelect(SelectEvent event) {
        gotoNextAnnotation();
    }
    
    public static void gotoNextAnnotation() {
        if(UserInfo.getInstance().getAssignmentMetaInfo() != null) {
            List<ProblemAnnotation> pids = UserInfo.getInstance().getAssignmentMetaInfo().getUnreadAnnotations();
            if(pids.size() > 0) {
                if(next > pids.size()-1) {
                    next = 0;
                }
                
                ProblemAnnotation annotation = pids.get(next);
                CatchupMath.getThisInstance().showAssignment(annotation.getAssignKey(), annotation.getPid());
                
                next++;
            }
            else {
                CmMessageBox.showAlert("There are no additional teacher notes available.");
            }
        }
    }
}
