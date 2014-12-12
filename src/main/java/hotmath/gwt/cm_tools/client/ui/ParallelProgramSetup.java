package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.rpc.action.SaveParallelProgramAction;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author bob
 *
 */
public class ParallelProgramSetup extends RegisterStudent {
	
    TextField nameFld;
    TextField passwordFld;
    
    Integer parellelProgId;
    ParallelProgramModel parallelProg;
    
	private CmAsyncRequest requestCallback;
    
	public ParallelProgramSetup(CmAsyncRequest callback, CmAdminModel adminModel) {
		// exclude Auto-Enroll Program unless in debug mode
	    super(null, adminModel, (CmCore.isDebug() == false));

	    doIt(callback);
	}	

	public ParallelProgramSetup(CmAsyncRequest callback,
			CmAdminModel adminModel, ParallelProgramModel ppModel,
			StudentModelI sm) {
		super(sm, adminModel);
		
		parellelProgId = ppModel.getId();
		parallelProg = ppModel;
		
		doIt(callback);
	}

	private void doIt(CmAsyncRequest callback) {
		requestCallback = callback;
		createWindow();
	    _window.setHeadingText("Parallel Program Setup");
	    
	    _fsProfile.clear();

	    _window.setHeight(440);
        
        nameFld = new TextField();  
        //nameFld.setFieldLabel("Name");
        nameFld.setAllowBlank(false);
        nameFld.setId("name");
        nameFld.setEmptyText("-- Enter Parallel Program name --");

        if (parallelProg != null) {
            nameFld.setValue((String)parallelProg.getName());
        }

	    _fsProfile.setHeadingText("Enter Name");
        _fsProfile.addThing(new MyFieldLabel(nameFld, "Name", LABEL_WIDTH, FIELD_WIDTH));
        _fsProfile.addThing(new HTML("<p>Student will Log In with your school Login Name, and use this name as a password.</p>"));

        _fsProgram.setHeadingText("Assign Program");
	    _formPanel.forceLayout();

	    // Don't allow Advanced Options (for now?)
	    hideAdvancedOptionsButton();

	    
	    showWindow();
	}

	public List<TextButton> getActionButtons() {
	    List<TextButton> list = new ArrayList<TextButton>();
        
	    if (parallelProg == null || parallelProg.getStudentCount() < 1) {
	    	TextButton save = new TextButton("Save", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    
                    if(!nameFld.isValid()) {
                        CmMessageBox.showAlert("Save Error", "Please provide all required fields.");
                        return;
                    }
	    			try {
	    				doSubmitAction(new AfterValidation() {

	    					@Override
	    					public void afterValidation(StudentModel student) {
	    					    
	    						student.setName(nameFld.getValue());
	    						student.setGroup(nameFld.getValue());
	    						student.setPasscode(nameFld.getValue());

                                 CmLogger.info("Student Model sent to savePPP: " + student.getName() + ", " + nameFld.getValue() + ", " + student);

	    						saveParallelProgramSetup(student);
	    					}
	    				});
	    			}
	    			catch(CmException cm) {
	    				// CmMessageBox.showAlert("First, make sure all values on form are valid");
	    			}
	    		}
	    	});
	    	list.add(save);
	    }
        
        
        TextButton close = new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _window.close();
            }
        });
        
        list.add(close);        
        
        return list;
	}

	private void saveParallelProgramSetup(StudentModel student) {
	    
	    if(!verifyOkToSave(student)) {
	        return;
	    }
	    
	    CmServiceAsync s = CmShared.getCmService();
        s.execute(new SaveParallelProgramAction(student.getAdminUid(), student, parellelProgId), new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                requestCallback.requestComplete("OK");
                _window.hide();
            }

            @Override
            public void onFailure(Throwable caught) {
                CmLogger.error("Error saving", caught);
                CmMessageBox.showAlert("Problem occurred while saving: " + caught.getMessage());
            }
        });	    
	}
}
