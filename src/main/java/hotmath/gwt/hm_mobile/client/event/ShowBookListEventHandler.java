package hotmath.gwt.hm_mobile.client.event;

import hotmath.gwt.hm_mobile.client.model.CategoryModel;

import com.google.gwt.event.shared.EventHandler;

public interface ShowBookListEventHandler extends EventHandler {
	void showBookList(CategoryModel category);
}
