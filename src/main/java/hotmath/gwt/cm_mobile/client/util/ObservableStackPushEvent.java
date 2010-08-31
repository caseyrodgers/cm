package hotmath.gwt.cm_mobile.client.util;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ObservableStackPushEvent<E> extends
		GwtEvent<ObservableStackPushEvent.ObservableStackPushHandler<E>> {

	E mItemPushed;

	public ObservableStackPushEvent(E itemPushed) {
		mItemPushed = itemPushed;
	}

	public E getItemPushed() {
		return mItemPushed;
	}

	// marker for handler logic in registration method
	public interface ObservableStackPushHandler<E> extends EventHandler {
		void itemPushed(ObservableStackPushEvent<E> event);
	}

	private static final GwtEvent.Type<?> TYPE = new GwtEvent.Type<ObservableStackPushEvent.ObservableStackPushHandler<?>>();

	@Override
	protected void dispatch(
			ObservableStackPushEvent.ObservableStackPushHandler<E> handler) {
		handler.itemPushed(this);
	}

	@Override
	public GwtEvent.Type<ObservableStackPushHandler<E>> getAssociatedType() {
		return (GwtEvent.Type<ObservableStackPushHandler<E>>) TYPE;
	}

	public static GwtEvent.Type<?> getType() {
		return TYPE;
	}

}
