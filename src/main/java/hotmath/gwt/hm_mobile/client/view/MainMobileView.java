package hotmath.gwt.hm_mobile.client.view;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;

public interface MainMobileView extends AcceptsOneWidget, IsWidget {
	
	public void setPresenter(Presenter presenter);

	/** Prepare the home for display
	 *  
	 */
	void setupView();
	
	public interface Presenter {
		void goTo(Place place);
		void navigateToBook();
		void searchForBook();
	}	
}
