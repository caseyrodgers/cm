package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;


/**
 * Provides form dialog for registering students in bulk
 * 
 * @author Bob
 * @author Casey
 *
 */
public class BulkStudentRegistrationWindow extends RegisterStudent {

	TextField groupFld;
	String _uploadKey; // = "upload_1250558647547";
	CmUploadForm _uploadForm;
	StudentModelI _student;

	/**
	 * Create a Bulk Registration Window
	 * 
	 * There is a bug with IE that you cannot have a form embedded in another
	 * form. Which is what happens when we embed the UploadField into the
	 * formPanel. This causes errors in IE on Windows (not macs?).
	 * 
	 * To deal with this we have to recreate the window, and add the formPanel,
	 * then the upload panel as siblings, not nested entities.
	 * 
	 * 
	 * @param sm
	 * @param cm
	 */
	public BulkStudentRegistrationWindow(StudentModel sm, CmAdminModel cm) {
		super(sm, cm);

		/**
		 * Reset the profile area
		 * 
		 */
		_fsProfile.clear();
		_fsProfile.addThing(new MyFieldLabel(groupCombo, "Group", LABEL_WIDTH, FIELD_WIDTH));
		_fsProfile.setHeadingText("Assign Group");
		_fsProgram.setHeadingText("Assign Program");
		stdAdvOptionsBtn.removeStyleName("register-student-advanced-options-btn");
		stdAdvOptionsBtn.addStyleName("register-student-advanced-options-bulk-reg-btn");
		customAdvOptionsBtn.removeStyleName("register-student-advanced-options-btn");
		customAdvOptionsBtn.addStyleName("register-student-advanced-options-bulk-reg-btn");

		/**
		 * Create the upload form, which will contain the upload field
		 * 
		 */
		_uploadForm = new CmUploadForm(cm.getUid(),
				new CmAsyncRequestImplDefault() {
					public void requestComplete(String uploadKey) {
						new AutoRegistrationWindow(_student, uploadKey);
					}
				});

		/**
		 * Create a new fieldset to hold the upload form
		 * 
		 */
		MyFieldSet fs = new MyFieldSet("Upload Students",FIELDSET_WIDTH);
		//fL.setLabelWidth(_formPanel.getLabelWidth());
        //fL.setDefaultWidth(LAYOUT_WIDTH);
		//fs.setWidth(_fsProfile.getWidth());
		fs.addThing(new MyFieldLabel(_uploadForm, "Upload File", LABEL_WIDTH, FIELD_WIDTH));
        fs.addThing(new HTML(
						"<p style='padding: 10px;'>Upload a TAB DELIMITED TEXT file or Excel spreadsheet with two columns; with the first " +
                        "column as the student name (e.g., Smith, John), and the second column as the password (e.g., 23242342). " +
					    "Use Excel's \"Save As...\" drop down menu to save as a Text (Tab delimited) file. </p>"));

		createWindow();
		//_window.clear();
		
		_window.setHeight(550);
		_window.setHeadingText("Bulk Registration");
        VerticalLayoutContainer verMain = new VerticalLayoutContainer();
        _formPanel.setWidget(verMain);
        
        verMain.add(_fsProfile);
        verMain.add(_fsProgram);
        
		//_formPanel.setHeight(305);
        //verPane.add(_formPanel);
		verMain.add(fs);
		
		
		FlowLayoutContainer flc = new FlowLayoutContainer();
		flc.setScrollMode(ScrollMode.AUTO);
		flc.add(verMain);
		setWidget(flc);


		
		//fl.setLabelWidth(_formPanel.getLabelWidth());
		//FormLayout fpL = (FormLayout) _fsProfile.getLayout();
		//fl.setDefaultWidth(fpL.getDefaultWidth());

		_window.show();
	}

	public List<TextButton> getActionButtons() {
		List<TextButton> list = new ArrayList<TextButton>();

		TextButton autoCreate = new TextButton("Upload");
		autoCreate.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				CatchupMathTools.setBusy(true);
				try {
					doSubmitAction(new AfterValidation() {
						@Override
						public void afterValidation(StudentModel student) {
							_student = student;
							if(!verifyOkToSave(student)) {
							    return;
							}
							if (!_uploadForm.isValid()) {
								CatchupMathTools
								.showAlert("Select a tab delimited file containing a list of names and passwords.");
								return;
							}
							_uploadForm.submit();
						}
					});
				} catch (CmException cme) {
					cme.printStackTrace();
				} finally {
					CatchupMathTools.setBusy(false);
				}
			}
		});

		list.add(autoCreate);

		TextButton close = new TextButton("Close");
		close.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
				_window.close();
			}
		});

		list.add(close);

		return list;
	}
}
