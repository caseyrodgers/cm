package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.SaveAutoRegistrationAction;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AutoRegisterStudentSetup extends RegisterStudent {
	
    TextField<String> _groupTag;
    TextField<String> _passwordTag;
    
	public AutoRegisterStudentSetup(StudentModel sm, CmAdminModel cm) {
	    super(sm, cm);
	    
	    _window.setHeading("Self Registration Setup");
	    _fsProfile.removeAll();

	    _window.setHeight(350);
        
        _groupTag = new TextField<String>();  
        _groupTag.setFieldLabel("Group Name");
        _groupTag.setAllowBlank(false);
        _groupTag.setId("groupTag");
        _groupTag.setEmptyText("-- Enter new group name --");
        _fsProfile.add(_groupTag);
        
        _fsProfile.add(new Html("<p>Student will Log In with your school Login Name, and use this Group name to self-register.</p>"));

        
        _fsProgram.setHeading("Assign Program for This Group");
	    _formPanel.layout();
	    showWindow();
	}
	

	protected List<Button> getActionButtons() {
	    List<Button> list = new ArrayList<Button>();
        
        Button autoCreate = new Button("Save");
        autoCreate  .addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                try {
                    doSubmitAction(_fsProgram, _formPanel, new AfterValidation() {
                        
                        @Override
                        public void afterValidation(StudentModel student) {
                            student.setName(_groupTag.getValue());
                            student.setGroup(_groupTag.getValue());
                            
                            saveAutoRegistrationSetup(student);
                        }
                    });
                }
                catch(CmException cm) {
                    // CatchupMathTools.showAlert("First, make sure all values on form are valid");
                }
            }
        });
        
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
	
	
	private void saveAutoRegistrationSetup(StudentModel student) {
	    CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new SaveAutoRegistrationAction(student.getAdminUid(), student), new AsyncCallback<RpcData>() {
                @Override
            public void onSuccess(RpcData result) {
                    _window.hide();
                    EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                CatchupMathTools.showAlert("Problem occurred while saving setup information: " + caught.getMessage());
            }
        });	    
	    
	}
}
