package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.LoginPanel;
import hotmath.gwt.cm.client.ui.NextPanelInfo;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.user.client.ui.Widget;

public class LoginContext  implements CmContext {

	public void setHeaderButtons(IconButton prevBtn, IconButton nextBtn) {
		prevBtn.setEnabled(false);
		nextBtn.setEnabled(false);
	}	
	
	public int getContextCompletionPercent() {
		return 0;
	}

	public String getContextHelp() {
		return "Login to Catchup Math";
	}

	public String getContextTitle() {
        return "";
	}
	
	public String getContextSubTitle() {
		return "";
	}

	public Widget getContextWidget() {
		return new LoginPanel();
	}

	public NextPanelInfo getNextPanelInfo() {
		return new LoginPanelNextDialog();
	}

	public void resetContext() {
	}

	public List<Component> getTools() {
		// TODO Auto-generated method stub
		return null;
	}

	public void doNext() {
		// nothing
	}

	public void doPrevious() {
		// nothing
	}
	public String getStatusMessage() {
	    return "Logging in";
	}
}