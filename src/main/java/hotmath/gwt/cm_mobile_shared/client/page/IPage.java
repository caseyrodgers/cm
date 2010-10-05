package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;

import java.util.List;

public interface IPage {

	String getTitle();

	String getBackButtonText();
	
	List<ControlAction> getControlFloaterActions();
}
