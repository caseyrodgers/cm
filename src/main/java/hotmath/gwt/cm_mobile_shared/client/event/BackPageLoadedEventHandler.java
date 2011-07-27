package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import com.google.gwt.event.shared.EventHandler;

public interface BackPageLoadedEventHandler extends EventHandler {
	void movedBack(IPage page);
}
