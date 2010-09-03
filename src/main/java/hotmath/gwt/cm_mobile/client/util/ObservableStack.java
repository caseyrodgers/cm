package hotmath.gwt.cm_mobile.client.util;

import java.util.Iterator;
import java.util.Stack;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/** A stack object that tracks and notifies
 *  listeners when objects are added or popped.
 *  
 * @author casey
 *
 * @param <E>
 */
public class ObservableStack<E> implements Iterable<E> {

	private HandlerManager mHandlerManager;

	int mCurrentMapId = 1;

	private Stack<E> mStack = new Stack<E>();

	public void push(E e) {
		mStack.push(e);

		firePushEvent(new ObservableStackPushEvent<E>(e));
	}

	public E pop() {
		E e = mStack.pop();

		firePopEvent(new ObservableStackPopEvent<E>(e));

		return e;
	}

	public E peek() {
		return mStack.peek();
	}

	public final HandlerRegistration addHandler(
			final ObservableStackPushEvent.ObservableStackPushHandler<E> handler) {
		return ensureHandlers()
				.addHandler(
						(GwtEvent.Type<ObservableStackPushEvent.ObservableStackPushHandler<E>>) ObservableStackPushEvent
								.getType(), handler);
	}

	public final HandlerRegistration addHandler(
			final ObservableStackPopEvent.ObservableStackPopHandler<E> handler) {
		return ensureHandlers()
				.addHandler(
						(GwtEvent.Type<ObservableStackPopEvent.ObservableStackPopHandler<E>>) ObservableStackPopEvent
								.getType(), handler);
	}

	private HandlerManager ensureHandlers() {
		return mHandlerManager == null ? mHandlerManager = new HandlerManager(
				this) : mHandlerManager;
	}

	void firePushEvent(
			GwtEvent<ObservableStackPushEvent.ObservableStackPushHandler<E>> event) {
		ensureHandlers().fireEvent(event);
	}

	void firePopEvent(
			GwtEvent<ObservableStackPopEvent.ObservableStackPopHandler<E>> event) {
		ensureHandlers().fireEvent(event);
	}

	@Override
	public Iterator<E> iterator() {
		return mStack.iterator();
	}

}
