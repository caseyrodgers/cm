package hotmath.gwt.hm_mobile.client.view;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface HomeView extends IsWidget  {
	public void showHome();
	
	void setPresenter(Presenter listener);
	
	public interface Presenter {
		void goTo(Place place);
	}	
}
