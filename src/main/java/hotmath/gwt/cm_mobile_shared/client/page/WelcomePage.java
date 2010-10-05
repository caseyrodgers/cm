package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;

import java.util.List;


public class WelcomePage implements IPage {

	@Override
	public String getTitle() {
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
}
