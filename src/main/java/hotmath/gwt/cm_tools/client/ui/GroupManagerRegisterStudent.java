package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class GroupManagerRegisterStudent extends RegisterStudent {
	
    GroupInfoModel gim;
    
	public GroupManagerRegisterStudent(StudentModelI sm, CmAdminModel cm, GroupInfoModel gim) {
	    super(sm, cm);
	
	    this.gim = gim;
	    
	    _window.setHeading("Program Reassignment");
	    _fsProfile.removeAll();

	    _window.setHeight(400);
        
	    _fsProfile.setHeading("Group Information");
        _fsProfile.add(new Label("Group: " + gim.getName()));
        _fsProfile.add(new Label("Number of Students: " + gim.getCount()));

        _formPanel.layout();
	    setVisible(true);
	    _window.setVisible(true);
	}
	

	protected List<Button> getActionButtons() {
	    List<Button> list = new ArrayList<Button>();
        
        Button autoCreate = new Button("Reassign Program");
        autoCreate  .addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                try {
                    doSubmitAction(_fsProgram, _formPanel, new AfterValidation() {
                        
                        @Override
                        public void afterValidation(StudentModel student) {
                            assignProgram(cmAdminMdl.getId(), gim.getId(), student);
                        }
                    });
                }
                catch(CmException cm) {
                    CatchupMathTools.showAlert("First, make sure all values on form are valid");
                }
            }
        });
        autoCreate.setToolTip("Re-enrolls all students in the group to the beginning of the specified program.");
        list.add(autoCreate);
        
        
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                _window.close();
            }
        });
        
        list.add(close);        
        
        return list;
	}
	
	
	
    private void assignProgram(final Integer adminId, final Integer groupId, final StudentModel studentTemplate) {
        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.GROUP_PROGRAM_ASSIGNMENT,adminId);
        action.setStudentModel(studentTemplate);
        action.setGroupId(groupId);
        action.setIsSelfReg(gim.getIsSelfReg()?1:0);
        cmService.execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                CmAdminDataReader.getInstance().fireRefreshData();
                _window.close();
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
    }
	
}
