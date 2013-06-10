package hotmath.gwt.cm_mobile_shared.client.activity;



import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewHandler;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData;
import hotmath.gwt.cm_mobile_shared.client.util.AssignmentData.CallbackWhenDataReady;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.view.AssignmentListView;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc_assignments.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.UpdateAssignmentViewEvent;
import hotmath.gwt.cm_rpc_assignments.client.event.UpdateAssignmentViewHandler;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import com.google.gwt.user.client.History;

public class AssignmentListActivity implements AssignmentListView.Presenter {

    static private AssignmentListActivity __lastInstance;
    static private AssignmentListView __lastView;
    public AssignmentListActivity() { }
    
    @Override
    public void readDataFromServer(final AssignmentListView view, boolean force) {
        __lastInstance = this;
        if(force) {
            AssignmentData.clear();
        }
        
        if(AssignmentData.getUserData() != null ) {
            showAssignments(view);
            return;
        }

        CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(true));
        AssignmentData.readAssData(new CallbackWhenDataReady() {
            @Override
            public void isReady() {
                CmRpcCore.EVENT_BUS.fireEvent(new SystemIsBusyEvent(false));
                showAssignments(view);
            }
        });
    }
    
    private void showAssignments(AssignmentListView view) {
        __lastView = view;
        view.displayAssigmments(AssignmentData.getUserData().getAssignments());        
    }
    
    @Override
    public void showAssignment(StudentAssignmentInfo studentAssignmentInfo) {
        studentAssignmentInfo.setChanged(false);
        checkIfAnyChanges();
        
        History.newItem("assignment:" + studentAssignmentInfo.getAssignKey() + ":" + System.currentTimeMillis());
    }
    
    

    public void checkIfAnyChanges() {
        /** check the latest info ... where ..?
         * 
         */
        AssignmentUserInfo ai = SharedData.getMobileUser().getAssignmentInfo();
        boolean changed=false;
        for(StudentAssignmentInfo si: AssignmentData.getUserData().getAssignments()) {
            if(si.isChanged()) {
                changed = true;
                break;
            }
        }
        ai.setChanged(changed);
        CmRpcCore.EVENT_BUS.fireEvent(new AssignmentsUpdatedEvent(ai));
    }


    
    /** Remove assignment data when logging in as new user
     * 
     */
    static {
        CmRpcCore.EVENT_BUS.addHandler(ShowLoginViewEvent.TYPE , new ShowLoginViewHandler() {
            @Override
            public void showLoginView() {
                AssignmentData.clear();
            }
        });
        
        CmRpcCore.EVENT_BUS.addHandler(UpdateAssignmentViewEvent.TYPE , new UpdateAssignmentViewHandler() {
            @Override
            public void updateView() {
                __lastInstance.readDataFromServer(__lastView, true);                
            }
        });
    }
}
