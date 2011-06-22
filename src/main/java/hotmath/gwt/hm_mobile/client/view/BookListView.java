package hotmath.gwt.hm_mobile.client.view;



import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookModel;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface BookListView extends IsWidget  {
	void showBookList(CmList<BookModel> books);
	
	void setPresenter(Presenter presenter);
	
	public interface Presenter {
		void goTo(Place place);
		void doLoadBookForSubject(String subject);
	}	
}
