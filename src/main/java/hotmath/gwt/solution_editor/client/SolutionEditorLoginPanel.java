package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class SolutionEditorLoginPanel extends Window {

	public interface SolutionEditorCallback {
		void loggedIn(SolutionEditorUser user);
	}

	TextField<String> _userName = new TextField<String>();
	TextField<String> _passWord = new TextField<String>();

	private SolutionEditorCallback callback;

	public SolutionEditorLoginPanel(String userName, 
			SolutionEditorCallback solutionEditorCallback) {
		this.callback = solutionEditorCallback;
		setModal(true);
		setClosable(false);
		setResizable(false);;
		setSize(340, 140);
		setHeading("Solution Editor Login");
		add(createFormPanel());
		
		_userName.setValue(userName);

		addButton(new Button("Login", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				doLogin();
			}
		}));
		setVisible(true);
	}
	

	private void messageBox(String msg) {
		MessageBox.alert("Error", msg, new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
			}
		});
	}
	
	private void doLogin() {
		if(!_userName.validate() || !_passWord.validate()) {
			messageBox("Username and password must be specified.");
			return;
		}
		
		String userName = _userName.getValue();
		String passWord = _passWord.getValue();
		
		if(!userName.equals(passWord)) {
			messageBox("Invalid password");
			return;
		}
		
		callback.loggedIn(new SolutionEditorUser(userName, passWord));
		hide();
	}

	private Component createFormPanel() {
		FormData formData = new FormData("-20");
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);

		FormPanel simple = new FormPanel();
		simple.setHeaderVisible(false);
		simple.setFrame(false);
		simple.setBodyBorder(false);
		simple.setWidth(300);
		simple.setLabelWidth(100);
		simple.setLabelAlign(LabelAlign.RIGHT);

		_userName.setFieldLabel("User Name");
		_userName.setAllowBlank(false);
		simple.add(_userName, formData);
		
		_passWord.setPassword(true);
		_passWord.setFieldLabel("Password");
		_passWord.setAllowBlank(false);
		simple.add(_passWord, formData);
		
		return simple;
	}

}
