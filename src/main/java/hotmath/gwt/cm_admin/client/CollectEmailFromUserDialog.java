package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class CollectEmailFromUserDialog extends CmWindow {

	TextField<String> email = new TextField<String>();
	Button closeButton = new Button("Close");

	public CollectEmailFromUserDialog() {
		setHeading("Email Address Needed");
		setSize(350, 150);
		email.setFieldLabel("Email Address");
		email.setAllowBlank(false);
		FormPanel form = new FormPanel();
		form.add(email);
		form.setLabelWidth(90);
		form.setHeaderVisible(false);
		form.setBodyBorder(false);
		form.setBorders(false);

		add(form);
		setModal(true);
		setClosable(false);
		setResizable(false);

		email.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null || (value.split("@").length != 2)) {
					return "Please enter a valid email address";
				}
				return null;
			}
		});

		Button save = new Button("Submit");
		addButton(save);
		addCloseButton();

		form.setButtonAlign(HorizontalAlignment.CENTER);

		FormButtonBinding binding = new FormButtonBinding(form);
		binding.addButton(save);

		setVisible(true);
	}
}
