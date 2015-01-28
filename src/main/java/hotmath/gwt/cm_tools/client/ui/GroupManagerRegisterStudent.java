package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class GroupManagerRegisterStudent extends RegisterStudent {
	
    GroupInfoModel gim;
    
	public GroupManagerRegisterStudent(StudentModelI sm, CmAdminModel cm, GroupInfoModel gim) {
	    super(sm, cm);
	
	    this.gim = gim;
	    
	    createWindow();
	    _window.setHeadingText("Program Reassignment");
	    _fsProfile.clear();

	    _window.setHeight(400);
        
	    _fsProfile.setHeadingText("Group Information");
        _fsProfile.addThing(new Label("Group: " + gim.getGroupName()));
        _fsProfile.addThing(new Label("Number of Students: " + gim.getStudentCount()));

        _formPanel.forceLayout();
	    setVisible(true);
	    _window.setVisible(true);
	}
	

	public List<TextButton> getActionButtons() {
	    List<TextButton> list = new ArrayList<TextButton>();
        
        TextButton autoCreate = new TextButton("Reassign Program");
        autoCreate.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                try {
                    doSubmitAction(new AfterValidation() {
                        
                        @Override
                        public void afterValidation(StudentModel student) {
                            assignProgram(cmAdminMdl.getUid(), gim.getId(), student);
                        }
                    });
                }
                catch(CmException cm) {
                    CmMessageBox.showAlert("First, make sure all values on form are valid");
                }
            }
        });
        autoCreate.setToolTip("Re-enrolls all students in the group to the beginning of the specified program.");
        list.add(autoCreate);
        
        
        TextButton close = new TextButton("Close", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                _window.close();
            }
        });
        
        list.add(close);        
        
        return list;
	}
	
	
	
    private void assignProgram(final Integer adminId, final Integer groupId, final StudentModel studentTemplate) {
        
        if(!verifyOkToSave(studentTemplate)) {
            return;
        }

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.GROUP_PROGRAM_ASSIGNMENT,adminId);                
                action.setStudentModel(studentTemplate);
                action.setGroupId(groupId);
                action.setIsSelfReg(gim.isSelfReg()?1:0);
                CmRpcCore.getCmService().execute(action, this);
            }
            
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                CmAdminDataReader.getInstance().fireRefreshData();
                _window.close();
            }
        }.register();
    }
	
}
