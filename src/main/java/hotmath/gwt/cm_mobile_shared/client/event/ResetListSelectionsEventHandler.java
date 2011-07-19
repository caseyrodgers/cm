package hotmath.gwt.cm_mobile_shared.client.event;

import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;

import com.google.gwt.event.shared.EventHandler;

public interface ResetListSelectionsEventHandler extends EventHandler {
	void resetSelections(GenericTextTag<String> tag);
}
