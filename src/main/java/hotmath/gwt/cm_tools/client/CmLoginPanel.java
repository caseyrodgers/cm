package hotmath.gwt.cm_tools.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CmLoginPanel extends Composite {

	// TextBox for the User Name
	private TextBox txtLogin = new TextBox();
	// PasswordTextBox for the password
	private PasswordTextBox txtPassword = new PasswordTextBox();
	// Error Label
	private Label lblError = new Label();

	public CmLoginPanel() {
		// Lets add a grid to hold all our widgets
		Grid grid = new Grid(4, 2);
		// Set the error label
		grid.setWidget(0, 1, lblError);
		// Add the Label for the username
		grid.setWidget(0, 0, new Label("Username"));
		// Add the UserName textBox
		grid.setWidget(1, 1, txtLogin);
		// Add the label for password
		grid.setWidget(2, 0, new Label("Password"));
		// Add the password widget
		grid.setWidget(2, 1, txtPassword);
		// Create a button
		Button btnLogin = new Button("login");
		// Add the Login button to the form
		grid.setWidget(3, 1, btnLogin);

		/**
		 * Add a click listener which is called when the button is clicked
		 */
		btnLogin.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				checkLogin(txtLogin.getText(), txtPassword.getText());
			}

		});
		initWidget(grid);
	}

	/**
	 * This method is called when the button is clicked
	 */
	private void checkLogin(String userName, String password) {
		setErrorText("Checking login for " + userName);
	}

	private void setErrorText(String errorMessage) {
		lblError.setText(errorMessage);
	}
}
