package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

public interface IPage {

	String getTitle();

	String getBackButtonText();
	
	List<ControlAction> getControlFloaterActions();
	
	TokenParser getBackButtonLocation();
}
