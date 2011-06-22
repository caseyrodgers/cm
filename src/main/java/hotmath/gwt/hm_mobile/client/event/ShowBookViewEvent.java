package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.hm_mobile.client.model.BookModel;

import com.google.gwt.event.shared.GwtEvent;

public class ShowBookViewEvent extends GwtEvent<ShowBookViewEventHandler> {
    BookModel book;
    
	public static Type<ShowBookViewEventHandler> TYPE = new Type<ShowBookViewEventHandler>();

	public ShowBookViewEvent(BookModel book) {
	    this.book = book;
	}

	@Override
	public Type<ShowBookViewEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowBookViewEventHandler handler) {
		handler.showBook(book);
	}
}
