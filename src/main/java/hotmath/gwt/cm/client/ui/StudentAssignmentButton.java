package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckEvent;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.assignment.GotoNextAnnotationButton;
import hotmath.gwt.shared.client.util.MyResources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class StudentAssignmentButton extends TextButton {
    
    MyResources resources = GWT.create(MyResources.class);
    
    enum ButtonState{HAS_ASSIGNMENTS, NO_ASSIGNMENTS};
    ButtonState _state;
    MenuItem itemAssignments, itemAnnotations;
    
    static StudentAssignmentButton __lastInstance;
    public StudentAssignmentButton() {
        super();
        __lastInstance = this;
        addStyleName("student-assignment-button");
        _state = ButtonState.NO_ASSIGNMENTS;
        
        setIcon(resources.assignmentNo());
        Menu menu = new Menu();
        itemAssignments = new MenuItem("Show Assignments");
        itemAssignments.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
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
        menu.add(itemAssignments);
        
        itemAnnotations = new MenuItem("You have no unread teacher notes");
        itemAnnotations.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                GotoNextAnnotationButton.gotoNextAnnotation();
            }
        });
        menu.add(itemAnnotations);
        setMenu(menu);
        
        addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CmRpcCore.EVENT_BUS.fireEvent(new ForceSystemSyncCheckEvent());
            }
        });
    }
    
    
    public void setState(AssignmentUserInfo assInfo) {
        
        if(assInfo.isChanged() || assInfo.getUnreadMessageCount() > 0) {
            startLoudButton();
        }
        else {
            stopLoudButton();
        }
        
        
        if(assInfo.getActiveAssignments() > 0 || assInfo.getUnreadMessageCount() > 0) {
            _state = ButtonState.HAS_ASSIGNMENTS;
            setIcon(resources.assignmentHas());
            itemAssignments.setEnabled(true);
            //changeStyle("student-assignment-button-new");
            
            int aa = assInfo.getActiveAssignments();
            int fa = assInfo.getUnreadMessageCount();
            String tip = "You have " + aa + " assignment" + (aa>1?"s":"") + "";
            itemAssignments.setText(tip);
            if(fa > 0) {
                itemAnnotations.setEnabled(true);
                String tipa = "You have " + fa + " new teacher note" + (fa>1?"s":"") + "";
                tip += tipa;
                itemAnnotations.setText(tipa);
            }
            else {
                itemAnnotations.setEnabled(false);
            }
        }
        else {
            _state = ButtonState.NO_ASSIGNMENTS;
            setIcon(resources.assignmentNo());
            //itemAssignments.setEnabled(false);            
            //changeStyle("student-assignment-button-no");
            
            //setToolTip("You do not have any assignments or teacher notes.");
        }
    }
    
    private void stopLoudButton() {
        isLoudRunning=false;
    }

    boolean isLoudRunning;
    public void startLoudButton() {
        if(!isLoudRunning) {
            isLoudRunning=true;
            doLoadButtonLoop();
        }
    }
    private void doLoadButtonLoop() {
        new Timer() {
            @Override
            public void run() {
                if(getIcon() == resources.assignmentHas()) {
                    setIcon(resources.assignmentHasAnnotation());
                }
                else {
                    setIcon(resources.assignmentHas());
                }
                if(isLoudRunning) {
                    doLoadButtonLoop();
                }
                else {
                    setState(UserInfo.getInstance().getAssignmentMetaInfo());
                }
            }
        }.schedule(1000);
    }
    
    public static void refreshButtonState() {
        __lastInstance.setState(UserInfo.getInstance().getAssignmentMetaInfo());
    }
}
