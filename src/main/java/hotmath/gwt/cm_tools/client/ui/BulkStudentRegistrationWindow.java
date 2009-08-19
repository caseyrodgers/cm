package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class BulkStudentRegistrationWindow extends RegisterStudent {
	
    TextField<String> groupFld;
    String _uploadKey; //  = "upload_1250558647547";
    FileUploadField _fileUpload;
    FormPanel _uploadForm;
    StudentModel _student;
    
	public BulkStudentRegistrationWindow(StudentModel sm, CmAdminModel cm) {
	    super(sm, cm);
	    
	    _window.setHeading("Bulk Registration");
	    
	    _fsProfile.remove(_fsProfile.getItemByItemId("name"));
	    _fsProfile.remove(_fsProfile.getItemByItemId("passcode"));
	    _fsProfile.setHeading("Assign Group");

	    _window.setHeight(525);

        _fsProgram.setHeading("Assign Program");
        
		FieldSet fs = new FieldSet();
		FormLayout fL = new FormLayout();
		fL.setLabelWidth(_formPanel.getLabelWidth());
        fL.setDefaultWidth(205);
        fs.setLayout(fL);
        fs.setHeading("Upload Students");

        _uploadForm = new FormPanel();
        _uploadForm.setFrame(false);
        _uploadForm.setStyleName("register-student-upload-form");
        _uploadForm.setStyleAttribute("padding-left", "0px");
        _uploadForm.setStyleAttribute("padding-top", "0px");
        _uploadForm.setStyleAttribute("padding-right", "0px");
        _uploadForm.setStyleAttribute("padding-bottom", "0px");
        _uploadForm.setStyleAttribute("padding", "0px");
        StringBuffer sb = new StringBuffer("/cm_admin/bulkRegister");
        sb.append("?aid=").append(cm.getId());
        _uploadForm.setAction(sb.toString());
        _uploadForm.setEncoding(Encoding.MULTIPART);  
        _uploadForm.setMethod(Method.POST);
        _uploadForm.setButtonAlign(HorizontalAlignment.CENTER);
        _uploadForm.setWidth(formWidth - 35);
        _uploadForm.setBodyBorder(false);
        _uploadForm.setLabelWidth(110);
        _uploadForm.setBorders(false);
        _uploadForm.setFieldWidth(295);
        _uploadForm.setHeaderVisible(false);
        _uploadForm.setShim(true);

        _uploadForm.addListener(Events.Submit, new Listener<FormEvent>() {

            public void handleEvent(FormEvent be) {

            	String response = be.getResultHtml();

                if(response.indexOf("<pre") != -1) {
                	int offset = response.indexOf(">") + 1;
                    response = response.substring(offset, response.length()-6);
                }
                JSONValue rspValue = JSONParser.parse(response);
                JSONObject rspObj  = rspValue.isObject();
                String status = rspObj.get("status").isString().stringValue();
                String msg = rspObj.get("msg").isString().stringValue();
                _uploadKey = rspObj.get("key").isString().stringValue();
                
                if(status.equals("Error")) {
                    CatchupMathTools.showAlert(msg);
                    return;
                }
                
                new AutoRegistrationWindow(_student, _uploadKey);
            }
        }); 

        _fileUpload = new FileUploadField();  
        _fileUpload.setAllowBlank(false);  
        _fileUpload.setFieldLabel("File");  
        _fileUpload.setAllowBlank(false);
        _fileUpload.setFieldLabel("File");
        _fileUpload.setAllowBlank(false);
        _fileUpload.setBorders(false);
        // apparently, setName() is required...
        _fileUpload.setName("bulk.reg.field");
        
        _uploadForm.add(_fileUpload);
        fs.add(_uploadForm);

	    fs.add(new Html("<p>Students will Log In with your school Login Name and the unique passwords you provide in the uploaded tab delimited text file.</p>"));
                
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
        autoCreate.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                try {
                    doSubmitAction(_fsProgram, _formPanel, new AfterValidation() {
                        //@Override
                        public void afterValidation(StudentModel student) {
                            _student = student;
                            
                            if(!_uploadForm.isValid()) {
                                CatchupMathTools.showAlert("Select a tab delimited file containing a list of names and passwords.");
                                return;
                            }
                            _uploadForm.submit();
                        }
                    });
                }
                catch(CmException cme) {
                    cme.printStackTrace();
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
}
