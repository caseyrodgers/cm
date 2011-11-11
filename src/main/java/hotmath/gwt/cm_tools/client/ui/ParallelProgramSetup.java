package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.rpc.action.SaveParallelProgramAction;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author bob
 *
 */
public class ParallelProgramSetup extends RegisterStudent {
	
    TextField<String> nameFld;
    TextField<String> passwordFld;
    
    Integer parellelProgId;
    ParallelProgramModel parallelProg;
    
	private CmAsyncRequest requestCallback;
    
	public ParallelProgramSetup(CmAsyncRequest callback, CmAdminModel adminModel) {
	    super(null, adminModel, true);

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
		
	    _window.setHeading("Parallel Program Setup");
	    
	    _fsProfile.removeAll();

	    _window.setHeight(350);
        
        nameFld = new TextField<String>();  
        nameFld.setFieldLabel("Name");
        nameFld.setAllowBlank(false);
        nameFld.setId("name");
        nameFld.setEmptyText("-- Enter Parallel Program name --");

        if (parallelProg != null) {
            nameFld.setValue((String)parallelProg.getName());
        }

	    _fsProfile.setHeading("");
        _fsProfile.add(nameFld);
        _fsProfile.add(new Html("<p>Student will Log In with your school Login Name, and use this name as a password.</p>"));

        _fsProgram.setHeading("Assign Program");
	    _formPanel.layout();

	    // Don't allow Advanced Options (for now?)
	    hideAdvancedOptionsButton();

	    
	    showWindow();
	}

	protected List<Button> getActionButtons() {
	    List<Button> list = new ArrayList<Button>();
        
	    if (parallelProg == null || parallelProg.getStudentCount() < 1) {
	    	Button save = new Button("Save");
	    	save.addSelectionListener(new SelectionListener<ButtonEvent>() {

	    		@Override
	    		public void componentSelected(ButtonEvent ce) {
	    			try {
	    				doSubmitAction(_fsProgram, _formPanel, new AfterValidation() {

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
	    				// CatchupMathTools.showAlert("First, make sure all values on form are valid");
	    			}
	    		}
	    	});

	    	list.add(save);
	    }
        
        
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

	private void saveParallelProgramSetup(StudentModel student) {
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
                CatchupMathTools.showAlert("Problem occurred while saving: " + caught.getMessage());
            }
        });	    
	}
}
