package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ListItem;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.ProblemNumber;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BookViewImpl extends AbstractPagePanel implements BookView, IPage {

    BookModel book;
    BookInfoModel info;

    @UiField
    ImageElement bookImage;

    @UiField
    SpanElement minPage, maxPage, pageProbNums, title, author;

    @UiField
    TextBox pageNumber;

    @UiField
    Button getProblems;

    @UiField
    HTMLPanel problemNumberDiv, messageDiv;

    @UiField
    HTMLPanel problemNumberList;
    
    
    @UiField
    SpanElement messageText;


    GenericContainerTag listItems = new GenericContainerTag("ul");

    private static BookViewImplUiBinder uiBinder = GWT.create(BookViewImplUiBinder.class);

    interface BookViewImplUiBinder extends UiBinder<Widget, BookViewImpl> {
    }

    public BookViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        problemNumberList.add(listItems);
        pageNumber.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent ke) {
                if (ke.getCharCode() == '\r') {
                    doGetProblems(null);
                }
            }
        });
        
        listItems.addStyleName("touch");
        
        pageNumber.setWidth("50px");
        addStyleName("bookViewImpl");
    }

    Presenter presenter;

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showBook(BookModel bookModel, BookInfoModel infoModel, int page) {
        problemNumberDiv.getElement().setAttribute("style", "display: none");

        this.book = bookModel;
        this.info = infoModel;

        String imagePath = null;
        if (bookModel.getImage() != null) {
            imagePath = "http://hotmath.com/images/books/" + bookModel.getImage();
            bookImage.setSrc(imagePath);
            bookImage.setAttribute("style", "display:block");
        } else {
        	bookImage.setAttribute("style", "display:none");
        }

        title.setInnerHTML(bookModel.getTitle());
        author.setInnerHTML(book.getAuthor());


        minPage.setInnerHTML("" + info.getMinPageNumber());
        maxPage.setInnerHTML("" + info.getMaxPageNumber());

        if(page == 0) {
        	page = info.getMinPageNumber();
        }
        pageNumber.setValue("" + page);
        doGetProblems(null);
    }
    
    private void setPageNumber(int page) {
        
        if(page > this.info.getMaxPageNumber() ) {
        	Window.alert("No more pages");
        	 pageNumber.setText(info.getMaxPageNumber() + "");
        }
        else if(page < info.getMinPageNumber()) {
        	Window.alert("No previous pages");
        	pageNumber.setText(info.getMinPageNumber() + "");
        }
        else {
        	book.setPage(Integer.parseInt(pageNumber.getValue()));
        	
            HmMobilePersistedPropertiesManager.getInstance().setLastBook(book);
            HmMobilePersistedPropertiesManager.getInstance().getBookPages().put(book.getTextCode(), page);
            
            HmMobilePersistedPropertiesManager.save();
            
        	pageNumber.setValue("" + page);
        	book.setPage(page);
        	presenter.getProblemNumbers(book, book.getPage());
        }
    }

    @UiHandler("getProblems")
    public void doGetProblems(ClickEvent event) {
    	String val = pageNumber.getValue();
    	if(val != null && val.length() > 0) {
    		try {
    			setPageNumber(Integer.parseInt(val));
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    			Window.alert("Error getting problems: " + e.getMessage());
    		}
    	}
    }
    
    @UiHandler("getNextProblems")
    public void doGetNextProblems(ClickEvent ce) {
        setPageNumber(book.getPage()+1);
    }
    
    @UiHandler("getPrevProblems")
    public void doGetPrevProblems(ClickEvent ce) {
        setPageNumber(book.getPage()-1);
    }

    
    class MyGenericTextTag extends GenericTextTag<String> {
        ProblemNumber pr;
        public MyGenericTextTag(ProblemNumber pr,TouchClickHandler<String> touchHandler) {
            super("li");
            addStyleName("group");
            addHandler(touchHandler);
            getElement().setInnerHTML(pr.getProblem());
            this.pr = pr;
        }
    }

    
	
    TouchClickHandler<String> touchHandler = new TouchClickHandler<String>() {
        @Override
        public void touchClick(TouchClickEvent<String> event) {
            presenter.loadSolution(((MyGenericTextTag)event.getTarget()).pr);
        }
    };

    @Override
    public void showProblemNumbers(CmList<ProblemNumber> problems) {
        listItems.clear();

        pageProbNums.setInnerHTML(pageNumber.getValue());
        
        if(problems.size() == 0) {
        	messageText.setInnerHTML("No problems found on page '" + pageNumber.getValue() + "'.");
        	messageDiv.getElement().setAttribute("style", "display: block");
        	problemNumberDiv.getElement().setAttribute("style", "display: none");
        }
        else {
        	messageDiv.getElement().setAttribute("style", "display: none");
	        problemNumberDiv.getElement().setAttribute("style", "display: block");
	        String problemSet = "___";
	        for (ProblemNumber pr : problems) {
	            if (!pr.getProblemSet().equals(problemSet)) {
	                problemSet = pr.getProblemSet();
	                ListItem li = new ListItem();
	                li.add(new HTMLPanel("<b>Set: " + problemSet + "</b>"));
	                listItems.add(li);
	            }
	            listItems.add(new MyGenericTextTag(pr,touchHandler));
	        }
        }
    }

    @Override
    public String getBackButtonText() {
        return "Back";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getTitle() {
        return "Book Problem Index";
    }
}


