package hotmath.gwt.cm_mobile.client.util;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ObservableStackPopEvent<E> extends
		GwtEvent<ObservableStackPopEvent.ObservableStackPopHandler<E>> {

	E mItemPoped;

	public ObservableStackPopEvent(E itemPoped) {
		mItemPoped = itemPoped;
	}

	public E getItemPoped() {
		return mItemPoped;
	}

	// marker for handler logic in registration method
	public interface ObservableStackPopHandler<E> extends EventHandler {
		void itemPoped(ObservableStackPopEvent<E> event);
	}

	private static final GwtEvent.Type<?> TYPE = new GwtEvent.Type<ObservableStackPopEvent.ObservableStackPopHandler<?>>();

	@Override
	protected void dispatch(
			ObservableStackPopEvent.ObservableStackPopHandler<E> handler) {
		handler.itemPoped(this);
	}

	@Override
	public GwtEvent.Type<ObservableStackPopHandler<E>> getAssociatedType() {
		return (GwtEvent.Type<ObservableStackPopHandler<E>>) TYPE;
	}

	public static GwtEvent.Type<?> getType() {
		return TYPE;
	}

}
