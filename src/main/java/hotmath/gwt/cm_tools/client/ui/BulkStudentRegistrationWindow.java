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
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BulkStudentRegistrationWindow extends RegisterStudent {
	
    TextField<String> groupFld;
    
	public BulkStudentRegistrationWindow(StudentModel sm, CmAdminModel cm) {
	    super(sm, cm);
	    
	    _window.setHeading("Bulk Registration");
	    
	    _fsProfile.remove(_fsProfile.getItemByItemId("name"));
	    _fsProfile.remove(_fsProfile.getItemByItemId("passcode"));
	    _fsProfile.setHeading("Assign Group");

	    _window.setHeight(520);

        _fsProgram.setHeading("Assign Program");
        
		FieldSet fs = new FieldSet();
		FormLayout fL = new FormLayout();
		fL.setLabelWidth(_formPanel.getLabelWidth());
        fL.setDefaultWidth(205);
        fs.setLayout(fL);
	    
        fs.setHeading("Upload Students");
        
        final FormPanel panel = new FormPanel();
        panel.setFrame(false);
        panel.setStyleName("register-student-upload-form");
        panel.setStyleAttribute("padding-left", "0px");
        panel.setStyleAttribute("padding-top", "0px");
        panel.setStyleAttribute("padding-right", "0px");
        panel.setStyleAttribute("padding-bottom", "0px");
        panel.setStyleAttribute("padding", "0px");
        panel.setAction("/bulkRegister");
        panel.setEncoding(Encoding.MULTIPART);  
        panel.setMethod(Method.POST);
        panel.setButtonAlign(HorizontalAlignment.CENTER);
        panel.setWidth(formWidth - 35);
        panel.setBodyBorder(false);
        panel.setLabelWidth(110);
        panel.setBorders(false);
        panel.setFieldWidth(295);
        panel.setHeaderVisible(false);
        panel.setShim(true);
        
        Button btn = new Button("Upload File");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {  

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!panel.isValid()) {
                    return;
                }

                // TODO: handle submit
                // panel.submit();

                MessageBox.info("Confirmation", "Your file was successfully uploaded", null);
            }
        });
        panel.addButton(btn);
   
        FileUploadField file = new FileUploadField();
        file.setAllowBlank(false);
        file.setFieldLabel("File");
        file.setAllowBlank(false);
        file.setBorders(false);
        panel.add(file);
        fs.add(panel);
	    fs.add(new Html("<p>Students will Log In with your school Login Name and the unique password you provide in the uploaded file.</p>"));
                
        _formPanel.add(fs);
        
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(_formPanel.getLabelWidth());
		FormLayout fpL = (FormLayout) _fsProfile.getLayout();
		fl.setDefaultWidth(fpL.getDefaultWidth());
		
	    _formPanel.layout();
	    setVisible(true);
	}

	protected List<Button> getActionButtons() {
	    List<Button> list = new ArrayList<Button>();
        
        Button autoCreate = new Button("Save");
        autoCreate  .addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                try {
                    doSubmitAction(_fsProgram, _formPanel, new AfterValidation() {
                        
                        //@Override
                        public void afterValidation(StudentModel student) {
                            student.setName(groupFld.getValue());
                            student.setGroup(groupFld.getValue());
                            
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
            //@Override
            public void onSuccess(RpcData result) {
                    _window.hide();
                    EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }

            //@Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                CatchupMathTools.showAlert("Problem occurred while saving setup information: " + caught.getMessage());
            }
        });	    
	    
	}
}
