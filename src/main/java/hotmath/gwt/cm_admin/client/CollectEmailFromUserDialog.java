package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.SaveAdminEmailAction;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;



public class CollectEmailFromUserDialog extends GWindow {

	TextField email = new TextField();
	TextField emailConfirm = new TextField();
	TextButton closeButton = new TextButton("Close");

	public CollectEmailFromUserDialog() {
		super(false);
		setHeadingText("Email Address Required");
		setPixelSize(350, 200);
		setModal(true);
		setClosable(false);
		setResizable(false);

		FramedPanel form = new FramedPanel();
		form.setHeaderVisible(false);
		FlowLayoutContainer flow = new FlowLayoutContainer();
		
		String html = "<p style='padding: 10px;'>Please provide your email address for occasional Catchup Math getting started tips and usage reports.</p>";
		flow.add(new HTML(html));
		
		email.setAllowBlank(false);
		flow.add(new MyFieldLabel(email, "Email Address", 100));

		class MyDateFieldValidator extends AbstractValidator<String> {
		    @Override
		    public List<EditorError> validate(Editor<String> editor, String value) {
				if (value == null || (value.split("@").length != 2)) {
					return createError(editor, "Please enter a valid email address.", value);
				}
		        return null;		    	
		    }
		}
		
		email.addValidator(new MyDateFieldValidator());
		
// 		emailConfirm.setFieldLabel("Email Confirm");
		emailConfirm.setAllowBlank(false);

		class MyDateCompareFieldValidator extends AbstractValidator<String> {
		    @Override
		    public List<EditorError> validate(Editor<String> editor, String value) {
				if (value == null || (!value.equals(email.getValue()))) {
					return createError(editor, "Email and Confirmation much match.", value);
				}
				return null;
		    }
		}
		emailConfirm.addValidator(new MyDateCompareFieldValidator());
		flow.add(new MyFieldLabel(emailConfirm, "Email Confirm", 100));

		form.setWidget(flow);

		setWidget(form);


		TextButton save = new TextButton("Save", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(isValid()) {
					saveEmail();
					close();
				}
			}
		});
		addButton(save);

		// form.setButtonAlign(HorizontalAlignment.CENTER);

		// FormButtonBinding binding = new FormButtonBinding(form);
		// binding.addButton(save);

		setVisible(true);
	}
	
	private boolean isValid() {
		return email.isCurrentValid() && emailConfirm.isCurrentValid();
	}

	private void saveEmail() {
		new RetryAction<StringHolder>() {
			@Override
			public void attempt() {
				SaveAdminEmailAction action = new SaveAdminEmailAction(UserInfoBase
						.getInstance().getUid(), email.getValue());
				setAction(action);
				CmRpcCore.getCmService().execute(action, this);
			}

			public void oncapture(StringHolder result) {
				CmLogger.info("Admin Email Saved: " + result.getResponse());
			}
		}.register();
	}

	public static void startTest() {
		new GwtTester(new TestWidget() {
			@Override
			public void runTest() {
				new CollectEmailFromUserDialog();				
			}
		});
		
	}
}
