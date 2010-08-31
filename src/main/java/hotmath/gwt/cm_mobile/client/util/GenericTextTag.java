package hotmath.gwt.cm_mobile.client.util;

//based on Label.java source code that comes with GWT

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class GenericTextTag<E> extends Widget implements HasText {

	private boolean mMovedAfterTouch = false;
	private E mAttachedInfo;

	private boolean mWantsEvents = false;
	private boolean mEventsWiredUp = false;
	HandlerRegistration mHandlerRegistration;

	public GenericTextTag(String tagName) {
		setElement(Document.get().createElement(tagName));
	}

	@Override
	protected void onLoad() {
		if (mWantsEvents) {
			wireUpEvents();
		}
	}

	private void wireUpEvents() {
		if (!mEventsWiredUp && this.isAttached()) {
			if (hasTouchEvent(this.getElement())) {
				registerDomTouchEvents();
			} else {
				// used for debugging:
				mHandlerRegistration = this.addDomHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						event.preventDefault();
						fireTouchClick();
					}
				}, ClickEvent.getType());
			}
			mEventsWiredUp = true;
		}
	}

	private void wireDownEvents() {
		if (mEventsWiredUp) {
			if (hasTouchEvent(this.getElement())) {
				unRegisterDomTouchEvents();
			} else {
				mHandlerRegistration.removeHandler();
			}
		}
		mEventsWiredUp = false;
	}

	@Override
	protected void onUnload() {
		wireDownEvents();
	}

	public GenericTextTag(String tagName, String text) {
		this(tagName);
		setText(text);
	}

	public void setAttachedInfo(E info) {
		mAttachedInfo = info;
	}

	public final HandlerRegistration addHandler(final TouchClickEvent.TouchClickHandler<E> handler) {
		if (!mWantsEvents) {
			mWantsEvents = true;
			wireUpEvents();
		}
		return this
				.addHandler(
						handler,
						(GwtEvent.Type<TouchClickEvent.TouchClickHandler<E>>) TouchClickEvent.getType());
	}

	public E getAttachedInfo() {
		return mAttachedInfo;
	}

	private native void registerDomTouchEvents() /*-{
		var instance = this;
		var element = this.@hotmath.gwt.cm_mobile.client.util.GenericTextTag::getElement()();

		element.ontouchstart = function(e){
				instance.@hotmath.gwt.cm_mobile.client.util.GenericTextTag::onDomTouchStart()();
		};

		element.ontouchmove = function(e){
				instance.@hotmath.gwt.cm_mobile.client.util.GenericTextTag::onDomTouchMove()();
		};

		element.ontouchend = function(e){
				instance.@hotmath.gwt.cm_mobile.client.util.GenericTextTag::onDomTouchEnd()();
		};
	}-*/;

	private native void unRegisterDomTouchEvents() /*-{
		var instance = this;
		var element = this.@hotmath.gwt.cm_mobile.client.util.GenericTextTag::getElement()();

		element.ontouchstart = null;

		element.ontouchmove = null;

		element.ontouchend = null;
	}-*/;

	public void onDomTouchStart() {
		mMovedAfterTouch = false;
	}

	public void onDomTouchMove() {
		mMovedAfterTouch = true;
	}

	public void onDomTouchEnd() {
		if (mMovedAfterTouch) {
			return;
		}

		fireTouchClick();
	}

	private void fireTouchClick() {
		fireEvent(new TouchClickEvent<E>(this));
	}

	public String getText() {
		return getElement().getInnerText();
	}

	public void setText(String text) {
		getElement().setInnerText(text);
	}

	private static native boolean hasTouchEvent(Element e) /*-{
		var ua = navigator.userAgent.toLowerCase();

		if (ua.indexOf("safari") != -1 &&
			ua.indexOf("applewebkit") != -1 &&
			ua.indexOf("mobile") != -1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}-*/;

}

