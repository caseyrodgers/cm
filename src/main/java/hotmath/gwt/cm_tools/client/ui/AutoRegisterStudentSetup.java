package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBoxGxt2;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.SaveAutoRegistrationAction;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class AutoRegisterStudentSetup extends RegisterStudent {
	
    TextField _groupTag;
    TextField _passwordTag;
    
	public AutoRegisterStudentSetup(StudentModel sm, CmAdminModel cm) {
	    super(sm, cm);
	    
	    createWindow();
	    _window.setHeadingText("Self Registration Setup");
	    _fsProfile.clear();
	    _window.setHeight(400);
        
        _groupTag = new TextField();  
        //_groupTag.setFieldLabel("Group Name");
        _groupTag.setAllowBlank(false);
        _groupTag.setId("groupTag");
        _groupTag.setEmptyText("-- Enter new group name --");
        _fsProfile.addThing(new MyFieldLabel(_groupTag, "Group Name", LABEL_WIDTH, FIELD_WIDTH));
        
        _fsProfile.addThing(new HTML("<p>Student will Log In with your school Login Name, and use this Group name to self-register.</p>"));
        
		stdAdvOptionsBtn.removeStyleName("register-student-advanced-options-btn");
		stdAdvOptionsBtn.addStyleName("register-student-advanced-options-self-reg-btn");
		customAdvOptionsBtn.removeStyleName("register-student-advanced-options-btn");
		customAdvOptionsBtn.addStyleName("register-student-advanced-options-self-reg-btn");
        
        _fsProgram.setHeadingText("Assign Program for This Group");
        _fsProgram.removeStyleName("register-student-outer-fieldset");
        _fsProgram.addStyleName("register-student-self-reg-outer-fieldset");
	    _formPanel.forceLayout();
	    
	    showWindow();
	}
	

	public List<TextButton> getActionButtons() {
	    List<TextButton> list = new ArrayList<TextButton>();
        
        TextButton autoCreate = new TextButton("Save");
        autoCreate.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                try {
                    if(!_groupTag.isValid()) {
                        CmMessageBox.showAlert("Group must be specified");
                        return;
                    }
                    doSubmitAction(new AfterValidation() {
                        
                        @Override
                        public void afterValidation(StudentModel student) {
                            student.setName(_groupTag.getValue());
                            student.setGroup(_groupTag.getValue());
                            
                            saveAutoRegistrationSetup(student);
                        }
                    });
                }
                catch(CmException cm) {
                    // CmMessageBoxGxt2.showAlert("First, make sure all values on form are valid");
                }
            }
        });
        
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
	
	
	private void saveAutoRegistrationSetup(StudentModel student) {
	    
	    String group = student.getGroup();
	    if(group == null) {
	        CmMessageBoxGxt2.showAlert("Group must be specified");
	        return;
	    }
	    
	    
	    if(!verifyOkToSave(student)) {
	        return;
	    }
	    
	    CmServiceAsync s = CmShared.getCmService();
        s.execute(new SaveAutoRegistrationAction(student.getAdminUid(), student), new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                    _window.hide();
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                CmMessageBox.showAlert("Problem occurred while saving setup information: " + caught.getMessage());
            }
        });	    
	}
}
