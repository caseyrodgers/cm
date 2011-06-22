package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.shared.EventHandler;

public interface LoadNewPageEventHandler extends EventHandler {
	void loadPage(IPage page);
}
