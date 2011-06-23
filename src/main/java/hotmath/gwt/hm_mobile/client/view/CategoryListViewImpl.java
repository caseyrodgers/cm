package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ListItem;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.ShowBookListEvent;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

public class CategoryListViewImpl extends AbstractPagePanel implements CategoryListView,IPage {
    
    
	Presenter presenter;
	
	public CategoryListViewImpl() {
	    
	    FlowPanel fp = new FlowPanel();
	    fp.add(new HTML("<h1>Select Subject</h1>"));
	    GenericContainerTag listItems = new GenericContainerTag("ul");

	    ClickHandler cl = new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                MyButton mb = (MyButton) arg0.getSource();
                String category = mb.getCategory();
                HmMobile.__clientFactory.getEventBus().fireEvent(new ShowBookListEvent(new CategoryModel(category)));
                
            }
        };
	    
	    listItems.add(new MyListItem(new MyButton("Pre-Algebra",cl)));
	    listItems.add(new MyListItem(new MyButton("Algebra 1",cl)));
        listItems.add(new MyListItem(new MyButton("Geometry",cl)));
        listItems.add(new MyListItem(new MyButton("Algebra 2",cl)));
        listItems.add(new MyListItem(new MyButton("Calculus",cl)));

        listItems.addStyleName("CategoryListViewImpl");

        fp.add(listItems);
	    initWidget(fp);
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
    
    
    @Override
    public String getTitle() {
        return "Available Subjects";
    }
}


class MyButton extends Button {
    String category;
    public MyButton(String name,ClickHandler cl) {
        super();
        this.category = name;
        setStyleName("sexybutton");
        addStyleName("sexybutton_xxl-aqua");
        getElement().setInnerHTML("<span><span>" + name + "</span></span>");
        
        addClickHandler(cl);
    }
    
    public String getCategory() {
        return category;
    }
}

class MyListItem extends ListItem {
    public MyListItem(MyButton myButton) {
        super();
        add(myButton);
    }
}