package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage.BackAction;

import java.util.List;


public class WelcomePage implements IPage {

	@Override
	public String getViewTitle() {
		return "Welcome Page";
	}

	@Override
	public String getBackButtonText() {
		return "Back to Welcome Page";
	}

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }
    
    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }  

    
    @Override
    public BackAction getBackAction() {
    	return null;
    }    
}
