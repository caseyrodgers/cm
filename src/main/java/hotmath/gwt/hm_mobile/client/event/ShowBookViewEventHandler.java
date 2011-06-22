package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.hm_mobile.client.model.BookModel;

import com.google.gwt.event.shared.EventHandler;

public interface ShowBookViewEventHandler extends EventHandler {
	void showBook(BookModel book);
}
