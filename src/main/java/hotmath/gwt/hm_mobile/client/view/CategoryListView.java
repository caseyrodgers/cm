package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

public interface CategoryListView extends AcceptsOneWidget, IsWidget {

    public void setCategoryList(List<CategoryModel> categories);

    public void setPresenter(Presenter presenter);
	public interface Presenter {
		void goTo(Place place);
		void doSubjectSelected(CategoryModel subject, Callback callback);
	}
	
	public interface Callback {
		void doBooksFind(CmList<BookModel> books);
	}
}
