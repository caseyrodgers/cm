package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
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
	
    TextField<String> _nameTag;
    TextField<String> _passwordTag;
    
	public ParallelProgramSetup(StudentModel sm, CmAdminModel cm) {
	    super(sm, cm);
	    
	    _window.setHeading("Parallel Program Setup");
	    _fsProfile.removeAll();

	    _window.setHeight(350);
        
        _nameTag = new TextField<String>();  
        _nameTag.setFieldLabel("Name");
        _nameTag.setAllowBlank(false);
        _nameTag.setId("nameTag");
        _nameTag.setEmptyText("-- Enter Parallel Program name --");
        _fsProfile.add(_nameTag);
        
        _fsProfile.add(new Html("<p>Student will Log In with your school Login Name, and use this name as a password.</p>"));

        
        _fsProgram.setHeading("Assign Program for This Parallel Program");
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
                            student.setName(_nameTag.getValue());
                            student.setGroup(_nameTag.getValue());
                            student.setPasscode(_nameTag.getValue());
                            
                            saveParallelProgramSetup(student);
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
	
	
	private void saveParallelProgramSetup(StudentModel student) {
	    CmServiceAsync s = CmShared.getCmService();
        s.execute(new SaveParallelProgramAction(student.getAdminUid(), student), new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                    _window.hide();
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                CatchupMathTools.showAlert("Problem occurred while saving: " + caught.getMessage());
            }
        });	    
	}
}
