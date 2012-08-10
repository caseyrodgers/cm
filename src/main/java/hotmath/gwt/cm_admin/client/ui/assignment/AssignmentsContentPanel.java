package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.DeleteAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsCreatedAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.Date;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AssignmentsContentPanel extends ContentPanel {
    
    ListView<Assignment, String> _listView;
    
    public AssignmentsContentPanel() {
        
        getHeader().addTool(createNewButton());
        getHeader().addTool(createEditButton());
        getHeader().addTool(createDelButton());
        setCollapsible(false);
        
        
        AssignmentProperties dp = GWT.create(AssignmentProperties.class);

        // Create the store that the contains the data to display in the grid
        ListStore<Assignment> s = new ListStore<Assignment>(dp.key());
        
       
        // Create the tree using the store and value provider for the name field
        _listView = new ListView<Assignment, String>(s, dp.assignmentName());
        add(_listView);
        
        
        readAssignmentData();
    }

    
    
    private void readAssignmentData() {
        new RetryAction<CmList<Assignment>>() {
            @Override
            public void attempt() {
                GetAssignmentsCreatedAction action = new GetAssignmentsCreatedAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<Assignment> assignments) {
                _listView.getStore().clear();
                _listView.getStore().addAll(assignments);
            }
        }.register();        
    }



    private void createNewAssignment() {
        
        Assignment newAss = new Assignment();
        newAss.setAssignmentName("My New Assignment: " + new Date());
        
        Date defaultDate = new Date();
        defaultDate.setHours(defaultDate.getHours() + 24);        
        newAss.setDueDate(defaultDate);
        
        new EditAssignmentDialog(newAss, new CallbackOnComplete() {
            @Override
            public void isComplete() {
                readAssignmentData();
            }
        });        
    }
    
    private void deleteSelectedAssignment() {
        
        final Assignment ass = _listView.getSelectionModel().getSelectedItem();
        if(ass == null) {
            return;
        }
        
        final ConfirmMessageBox cm = new ConfirmMessageBox("Delete Assignment", "Are you sure you want to delete this assignment?");
        cm.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (cm.getHideButton() == cm.getButtonById(PredefinedButton.YES.name())) {
                    deleteAssignment(ass);
                }
            }
        });
        cm.setWidth(300);
        cm.show();
    }

    private void deleteAssignment(final Assignment ass) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                DeleteAssignmentAction action = new DeleteAssignmentAction(ass.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CmBusyManager.setBusy(false);
                _listView.getStore().remove(ass);
            }
        }.register();        
    }
    
    private void editCurrentAssignment() {
        Assignment data = _listView.getSelectionModel().getSelectedItem();
        if(data != null) {
            new EditAssignmentDialog(data, new CallbackOnComplete() {
                @Override
                public void isComplete() {
                    readAssignmentData();
                }
            });        
        }
        
    }

    
    private Widget createDelButton() {
        TextButton btn = new TextButton("Del");
        btn.setToolTip("Delete selected assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                deleteSelectedAssignment();
            }

        });
        return btn;
    }
    
    private Widget createEditButton() {
        TextButton btn = new TextButton("Edit");
        btn.setToolTip("Edit selected assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                editCurrentAssignment();
            }

        });
        return btn;
    }
        
    private Widget createNewButton() {
        TextButton btn = new TextButton("New");
        btn.setToolTip("Create new assignment");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
             createNewAssignment();
            }

        });
        return btn;
    }

}

