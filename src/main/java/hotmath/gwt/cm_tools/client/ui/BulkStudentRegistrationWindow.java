package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * Provides form dialog for registering students in bulk
 * 
 * @author Bob
 * @author Casey
 *
 */
public class BulkStudentRegistrationWindow extends RegisterStudent {

	TextField<String> groupFld;
	String _uploadKey; // = "upload_1250558647547";
	FileUploadField _fileUpload;
	CmUploadForm _uploadForm;
	StudentModel _student;

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
		_fsProfile.remove(_fsProfile.getItemByItemId("name"));
		_fsProfile.remove(_fsProfile.getItemByItemId("passcode"));
		_fsProfile.setHeading("Assign Group");
		_fsProgram.setHeading("Assign Program");

		/**
		 * Create the upload form, which will contain the upload field
		 * 
		 */
		_uploadForm = new CmUploadForm(cm.getId(),
				new CmAsyncRequestImplDefault() {
					public void requestComplete(String uploadKey) {
						new AutoRegistrationWindow(_student, uploadKey);
					}
				});

		/**
		 * Create a new fieldset to hold the upload form
		 * 
		 */
		FieldSet fs = new FieldSet();
		FormLayout fL = new FormLayout();
		fL.setLabelWidth(_formPanel.getLabelWidth());
		fs.setLayout(fL);
		fs.setHeading("Upload Students");
		fs.setWidth(_fsProfile.getWidth());
		fs.add(_uploadForm);
		fs
				.add(new Html(
						"<p>Students will Log In with your school Login Name and the unique passwords you provide in the uploaded tab delimited text file.</p>"));

		_window.removeAll();
		_window.setLayout(new BorderLayout());
		_window.setHeight(470);
		_window.setWidth(550);
		_window.setHeading("Bulk Registration");

		_formPanel.setHeight(305);
		_window.add(_formPanel, new BorderLayoutData(LayoutRegion.CENTER, 300));
		_window.add(fs, new BorderLayoutData(LayoutRegion.SOUTH, 150));

		FormLayout fl = new FormLayout();
		fl.setLabelWidth(_formPanel.getLabelWidth());
		FormLayout fpL = (FormLayout) _fsProfile.getLayout();
		fl.setDefaultWidth(fpL.getDefaultWidth());

		_window.show();
	}

	protected List<Button> getActionButtons() {
		List<Button> list = new ArrayList<Button>();

		Button autoCreate = new Button("Upload");
		autoCreate.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				CatchupMathTools.setBusy(true);
				try {
					doSubmitAction(_fsProgram, _formPanel,
							new AfterValidation() {
								@Override
								public void afterValidation(StudentModel student) {
									_student = student;

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
