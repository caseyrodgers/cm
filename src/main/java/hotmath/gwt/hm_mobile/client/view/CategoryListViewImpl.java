package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.ShowBookListEvent;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

public class CategoryListViewImpl extends AbstractPagePanel implements
		CategoryListView, IPage {

	Presenter presenter;

	public CategoryListViewImpl() {
		FlowPanel fp = new FlowPanel();
		listItems.addStyleName("touch");
		
		TouchClickHandler<String> touchHandler = new TouchClickHandler<String>() {
			@Override
			public void touchClick(TouchClickEvent<String> event) {
				MyGenericTextTag m = (MyGenericTextTag) event.getSource();
					String category = m.getCategory();
					HmMobile.__clientFactory.getEventBus().fireEvent(
							new ShowBookListEvent(new CategoryModel(category)));
			}
		};
		
		listItems.add(new MyGenericTextTag("Middle Math Series",touchHandler));
		listItems.add(new MyGenericTextTag("Pre-Algebra",touchHandler));
		listItems.add(new MyGenericTextTag("Algebra 1",touchHandler));
		listItems.add(new MyGenericTextTag("Geometry",touchHandler));
		listItems.add(new MyGenericTextTag("Algebra 2",touchHandler));
		listItems.add(new MyGenericTextTag("Science",touchHandler));
		listItems.add(new MyGenericTextTag("Trigonometry",touchHandler));
		listItems.add(new MyGenericTextTag("Precalculus",touchHandler));
		listItems.add(new MyGenericTextTag("College Algebra",touchHandler));
		listItems.add(new MyGenericTextTag("Calculus",touchHandler));
		listItems.addStyleName("CategoryListViewImpl");
		
		fp.add(listItems);
		initWidget(fp);
	}
	
	
    class MyGenericTextTag extends GenericTextTag<String> {
        String category;
        public MyGenericTextTag(String category,TouchClickHandler<String> touchHandler) {
            super("li");
            addStyleName("group");
            addHandler(touchHandler);
            getElement().setInnerHTML("<span>" + category + "</span>");
            this.category = category;
        }
    	public String getCategory() {
    		return category;
    	}        
    }

	@Override
	public void setCategoryList(List<CategoryModel> categories) {
		// dataProvider.setList(categories);
	}

	public void catPreAlgebra(ClickEvent handler) {
		HmMobile.__clientFactory.getPlaceController().goTo(
				new BookListPlace(""));
	}

	@Override
	public void setWidget(IsWidget arg0) {
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public String getBackButtonText() {
		return "";
	}

	@Override
	public List<ControlAction> getControlFloaterActions() {
		return null;
	}

	@Override
	public TokenParser getBackButtonLocation() {
		return new TokenParser();
	}

	@Override
	public String getTitle() {
		return "Hotmath Mobile";
	}

	@Override
	public void resetView() {
		super.resetListSelections();
	}
}
