package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.TextField;



public class SolutionEditorLoginPanel extends GWindow {

	public interface SolutionEditorCallback {
		void loggedIn(SolutionEditorUser user);
	}

	TextField _userName = new TextField();
	PasswordField _passWord = new PasswordField();

	private SolutionEditorCallback callback;

	public SolutionEditorLoginPanel(String userName, SolutionEditorCallback solutionEditorCallback) {
	    super(false);
		this.callback = solutionEditorCallback;
		
		setModal(true);
		setClosable(false);
		setResizable(false);;
		setPixelSize(340, 140);
		setHeadingText("Solution Editor Login");
		setWidget(createFormPanel());
		
		_userName.setValue(userName);

		addButton(new TextButton("Login", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
				doLogin();
			}
		}));
		setVisible(true);
	}
	

	private void messageBox(String msg) {
		CmMessageBox.showAlert("Error", msg);
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

	    FramedPanel frame = new FramedPanel();
		
	    frame.setHeaderVisible(false);
	    
	    FlowLayoutContainer flow = new FlowLayoutContainer();
		//_userName.setFieldLabel("User Name");
		_userName.setAllowBlank(false);
		flow.add(new FieldLabel(_userName, "User Name"));
		
		//_passWord.setFieldLabel("Password");
		_passWord.setAllowBlank(false);
		flow.add(new FieldLabel(_passWord, "Password"));
		frame.setWidget(flow);
		
		return frame;
	}

}
