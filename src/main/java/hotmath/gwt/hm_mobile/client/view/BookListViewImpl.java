package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.ShowBookViewEvent;
import hotmath.gwt.hm_mobile.client.model.BookModel;

import java.util.List;

import com.google.gwt.user.client.ui.SimplePanel;

public class BookListViewImpl extends AbstractPagePanel implements BookListView,IPage {

	CmList<BookModel> books;

	String _subject;
	Presenter presenter;

	GenericContainerTag listItems = new GenericContainerTag("ul");
	
	public BookListViewImpl() {
		SimplePanel sp = new SimplePanel();
		sp.add(listItems);
	
		initWidget(sp);
        addStyleName("bookListViewImpl");
        
        listItems.addStyleName("touch");
        listItems.addStyleName("large");
	}
	
    class MyGenericTextTag extends GenericTextTag<String> {
        BookModel book;
        public MyGenericTextTag(BookModel bm) {
            super("li");
            this.book = bm;
        }
    }
	

	@Override
    public void showBookList(String subject, CmList<BookModel> books) {
		_subject = subject;
		getWidget().getElement().setAttribute("style","display:none");
		this.books = books;
		listItems.clear();
		for(BookModel bm: books) {
			String image = "";
			if(bm.getImage() != null) {
				image = "<img src='/images/books/" + bm.getImage() + "'/>";
			}
			
			String pubDate = "";
			if(bm.getPubDate() != null) {
				pubDate = " (" + bm.getPubDate() + ")";
			}
			
			
			String title=null;
			if(bm.getTitle() == null) {
				title = "(no title, " + bm.getTextCode() + ")";
			}
			else {
				title = bm.getTitle();
			}
			
			String publisher="";
			if(bm.getPublisher() != null) {
				publisher = bm.getPublisher();
			}
			
			String author="";
			if(bm.getAuthor() != null) {
				author = bm.getAuthor();
			}
			
			String html = 
				"<div class='book-item'>" +
			    image + 
				"  <div class='book-item-box'>" +
				"      <div>" + title + "</div>" +
				"      <div>" + 
				           publisher + pubDate + 
			    "      </div>" +
				"      <div>" + 
  		                   author + 
	            "      </div>" +
			    
			    "  </div>" +
			    "</div>";
			
			
			GenericTextTag<String> tt = new MyGenericTextTag(bm);
			tt.getElement().setInnerHTML(html);
			tt.addStyleName("group");
			
			tt.addHandler(new TouchClickHandler<String>() {
			    @Override
			    public void touchClick(TouchClickEvent<String> event) {
			        BookModel bm = ((MyGenericTextTag)event.getTarget()).book;
			        
			        HmMobile.__clientFactory.getEventBus().fireEvent(new ShowBookViewEvent(bm));
			    }
            });
			
			listItems.add(tt);
		}
		
		getWidget().getElement().setAttribute("style","display:block");
    }
//	
//	@UiHandler("list")
//	public void onListSelection(SelectionChangedEvent event) {
//		int selection = event.getSelection();
//		BookModel book = books.get(selection);
//		
//		BookViewActivity bookActivity = new BookViewActivity(new MobilePlace(""), HmMobile.__clientFactory);
//		BookView bookView = HmMobile.__clientFactory.getBookView();
//		bookView.setPresenter(bookActivity);
//		bookActivity.loadBookInfo(this,book);
//	}

	@Override
    public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
    }

    @Override
    public String getBackButtonText() {
        return "<<";
    }
    
    @Override
    public BackAction getBackAction() {
    	return new BackAction() {
			@Override
			public void goBack() {
				HmMobile.__clientFactory.getEventBus().fireEvent(new BackDiscoveryEvent((IPage)BookListViewImpl.this));
			}
		};
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }
    
    @Override
    public String getTitle() {
        return  _subject;
    }
    
    @Override
    public void resetView() {
    	super.resetListSelections();
    }
}
