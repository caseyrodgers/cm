package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.ShowBookListEvent;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.IsWidget;

public class CategoryListViewImpl extends AbstractPagePanel implements CategoryListView,IPage {
	Presenter presenter;
	
	public CategoryListViewImpl() {
	    GenericContainerTag listItems = new GenericContainerTag("ul");
        listItems.add(new MyListItem("Pre-Algebra"));
	    listItems.add(new MyListItem("Algebra 1"));
        listItems.add(new MyListItem("Geometry"));
        listItems.add(new MyListItem("Algebra 2"));
        listItems.add(new MyListItem("Calculus"));
	    initWidget(listItems);
	}

	class MyListItem extends GenericTextTag<String> {
	    public MyListItem(String text) {
	        super("li");
	        setText(text);
	        
	        addHandler(new TouchClickHandler<String>() {
	            @Override
	            public void touchClick(TouchClickEvent<String> event) {
	                CategoryModel category = new CategoryModel(event.getTarget().getText());
	                HmMobile.__clientFactory.getEventBus().fireEvent(new ShowBookListEvent(category));
	            }
            });
	    }
	}
	
	@Override
    public void setCategoryList(List<CategoryModel> categories) {
			//dataProvider.setList(categories);
    }
	
	public void catPreAlgebra(ClickEvent handler) {
		HmMobile.__clientFactory.getPlaceController().goTo(new BookListPlace(""));
	}

	@Override
    public void setWidget(IsWidget arg0) {
    }
	
//	@UiHandler("list")
//	public void onListSelection(SelectionChangedEvent event) {
//		int selection = event.getSelection();
//		String sn = ((Label)((ListItem)list.getWidget(selection)).getWidget(0)).getText();
//		CategoryModel subject = new CategoryModel(sn);
//		presenter.doSubjectSelected(subject, new Callback() {
//			
//			@Override
//			public void doBooksFind(CmList<BookModel> books) {
//				
//			}
//		});
//	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return new TokenParser();
    }
}
