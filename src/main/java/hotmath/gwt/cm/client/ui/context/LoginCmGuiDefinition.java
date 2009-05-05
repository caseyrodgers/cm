package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.CmGuiDefinition;
import hotmath.gwt.cm.client.ui.ContextController;
import hotmath.gwt.cm.client.ui.LoginPanel;
import hotmath.gwt.cm.client.ui.NextPanelInfoImplDefault;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


/**
 * Define the Quiz Panel 
 * 
 * @TODO: which quiz?
 */
public class LoginCmGuiDefinition implements CmGuiDefinition {
	
	public String getTitle() {
		return "Login to Catchup Math";
	}
	
	public Widget getWestWidget() {
		LayoutContainer cp = new LayoutContainer();
		cp.setStyleName("login-resource-page");
		HTML html = new HTML("<h2>Welcome to Catchup Math!</h2>");
		cp.add(html);
		
		ContextController.getInstance().setCurrentContext(getContext());
		return cp;
	}

	public Widget getCenterWidget() {
		return new LoginPanel();
	}

	public CmContext getContext() {
		return new LoginContext();
	}
}

class LoginPanelNextDialog extends NextPanelInfoImplDefault {

	public void doNext() {
		CatchupMath.showAlert("Doing Next");
	}

	public Widget getNextPanelWidget() {
		return new Label("Login Next");
	}
}