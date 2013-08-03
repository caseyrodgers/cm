package hotmath.gwt.hm_mobile.client.view;



import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.ResettablePage;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface LoginView extends ResettablePage, IsWidget, IPage {
	void setPresenter(Presenter presenter, CallbackOnComplete callbackOnComplete);
	public interface Presenter {
		void goTo(Place place);
        void doLogin(String userName, String password);
        void setupDemoMode();
	}	
}
