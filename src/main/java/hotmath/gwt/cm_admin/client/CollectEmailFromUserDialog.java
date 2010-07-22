package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.SaveAdminEmailAction;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class CollectEmailFromUserDialog extends CmWindow {

	TextField<String> email = new TextField<String>();
	TextField<String> emailConfirm = new TextField<String>();
	Button closeButton = new Button("Close");

	public CollectEmailFromUserDialog() {
		setHeading("Email Address Required");
		setSize(350, 200);
		setModal(true);
		setClosable(false);
		setResizable(false);

		String html = "<p style='padding: 10px;'>Before you can begin using Catchup Math you will need to provide a valid email address.</p>";
		add(new Html(html));

		FormPanel form = new FormPanel();
		form.setLabelWidth(90);
		form.setHeaderVisible(false);
		form.setBodyBorder(false);
		form.setBorders(false);
		
		email.setFieldLabel("Email Address");
		email.setAllowBlank(false);
		form.add(email);
		

		email.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null || (value.split("@").length != 2)) {
					return "Please enter a valid email address.";
				}
				return null;
			}
		});

		emailConfirm.setFieldLabel("Email Confirm");
		emailConfirm.setAllowBlank(false);
		emailConfirm.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null || (!value.equals(email.getValue()))) {
					return "Email and Confirmation much match.";
				}
				return null;
			}
		});
		
		form.add(emailConfirm);

		add(form);


		Button save = new Button("Save");
		save.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				saveEmail();
				close();
			}
		});
		addButton(save);

		form.setButtonAlign(HorizontalAlignment.CENTER);

		FormButtonBinding binding = new FormButtonBinding(form);
		binding.addButton(save);

		setVisible(true);
	}

	private void saveEmail() {
		new RetryAction<StringHolder>() {
			@Override
			public void attempt() {
				SaveAdminEmailAction action = new SaveAdminEmailAction(UserInfoBase
						.getInstance().getUid(), email.getValue());
				setAction(action);
				CmShared.getCmService().execute(action, this);
			}

			public void oncapture(StringHolder result) {
				CmLogger.info("Admin Email Saved: " + result.getResponse());
			}
		}.register();
	}
}
