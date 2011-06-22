package hotmath.gwt.hm_mobile.client.activity;

import hotmath.gwt.hm_mobile.client.ClientFactory;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.place.CategoryListPlace;
import hotmath.gwt.hm_mobile.client.view.CategoryListView;
import hotmath.gwt.hm_mobile.client.view.CategoryListView.Callback;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class CategoryListActivity extends AbstractActivity implements CategoryListView.Presenter {
	private ClientFactory clientFactory;
	private String token;

	List<CategoryModel> _categories = new ArrayList<CategoryModel>(){{
		add(new CategoryModel("Pre-Algebra"));
		add(new CategoryModel("Algebra 1"));
		add(new CategoryModel("Geometry"));
		add(new CategoryModel("Algebra 2"));
		add(new CategoryModel("Calculus"));
	}};
	
	public CategoryListActivity(CategoryListPlace place, ClientFactory clientFactory) {
		this.token = place.getToken();
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		CategoryListView view = clientFactory.getCategoryListView();
		containerWidget.setWidget(view.asWidget());
		
		view.setCategoryList(_categories);		
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
		return null; // "Are you sure you want to stop?";
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	
	@Override
    public void doSubjectSelected(CategoryModel subject, final Callback callback) {
    }
}