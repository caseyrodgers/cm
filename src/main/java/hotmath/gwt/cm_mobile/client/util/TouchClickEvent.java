package hotmath.gwt.cm_mobile.client.util;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class TouchClickEvent<E> extends
		GwtEvent<TouchClickEvent.TouchClickHandler<E>> {

	GenericTextTag<E> mTarget;

	public TouchClickEvent(GenericTextTag<E> target) {
		mTarget = target;
	}

	public GenericTextTag<E> getTarget() {
		return mTarget;
	}

	// marker for handler logic in registration method
	public interface TouchClickHandler<E> extends EventHandler {
		void touchClick(TouchClickEvent<E> event);
	}

	private static final GwtEvent.Type<?> TYPE = new GwtEvent.Type<TouchClickHandler<?>>();

	@Override
	protected void dispatch(TouchClickHandler<E> handler) {
		handler.touchClick(this);
	}

	@Override
	public GwtEvent.Type<TouchClickHandler<E>> getAssociatedType() {
		return (GwtEvent.Type<TouchClickHandler<E>>) TYPE;
	}

	public static GwtEvent.Type<?> getType() {
		return TYPE;
	}

}
