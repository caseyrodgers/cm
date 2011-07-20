package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.cm_mobile_shared.client.event.ResetListSelectionsEventHandler;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;

import com.google.gwt.event.shared.GwtEvent;

public class ResetListSelections extends GwtEvent<ResetListSelectionsEventHandler> {
	
	GenericTextTag<String> tag;
	public static Type<ResetListSelectionsEventHandler> TYPE = new Type<ResetListSelectionsEventHandler>();

	public ResetListSelections(GenericTextTag<String> tag) {
		this.tag = tag;
	}

	@Override
	public Type<ResetListSelectionsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ResetListSelectionsEventHandler handler) {
		//handler.resetSelections(tag);
	}
}
