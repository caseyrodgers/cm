package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ListItem;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.event.ResetListSelectionsEventHandler;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.ToolTipListener;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.ResetListSelections;
import hotmath.gwt.hm_mobile.client.model.BookInfoModel;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;

import java.util.Iterator;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BookViewImpl extends AbstractPagePanel implements BookView, IPage {

    BookModel book;
    BookInfoModel info;
    boolean inited=false;
    
    ToolTipListener _toolTipListener = new ToolTipListener("", 3000);
    GenericContainerTag listItems = new GenericContainerTag("ul");

    private static BookViewImplUiBinder uiBinder = GWT.create(BookViewImplUiBinder.class);

    interface BookViewImplUiBinder extends UiBinder<Widget, BookViewImpl> {
    }


    public BookViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        pageNumber.getElement().setAttribute("type","number");
        
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
        
        addStyleName("bookViewImpl");

        /** only allow numeric entry */
        pageNumber.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
              if (!Character.isDigit(event.getCharCode())) {
                ((TextBox) event.getSource()).cancelKey();
              }
            }
          });

    	pageNumber.addMouseListener(_toolTipListener);
    }

    
    Presenter presenter;

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    
    @Override
    public BookModel getLoadedBookModel() {
    	return this.book;
    }
    
    
    @Override
    public void showBook(BookModel bookModel, BookInfoModel infoModel, int page) {
    	
    	if(!inited) {
    		/** done here to avoid null with __clientFactory during constructor */
        	HmMobile.__clientFactory.getEventBus().addHandler(ResetListSelections.TYPE,new ResetListSelectionsEventHandler() {
    			@Override
    			public void resetSelections(GenericTextTag<String> tag) {
    				resetListSelections(tag);
    			}
    		});
        	inited=true;
    	}

    	problemNumberDiv.getElement().setAttribute("style", "display: none");

        this.book = bookModel;
        this.info = infoModel;

        _toolTipListener.setToolTip("Enter a page number between " + info.getMinPageNumber() + " and " + info.getMaxPageNumber() + ".");

        String imagePath = null;
        if (bookModel.getImage() != null) {
            imagePath = "/images/books/" + bookModel.getImage();
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
    
    private void resetListSelections(GenericTextTag<String> tag) {
    	Log.debug("Resetting list selections: " + tag.getClass().getName());
    	Iterator<Widget> it = listItems.iterator();
    	while(it.hasNext()) {
    		Widget w = it.next();
    		if(w instanceof MyGenericTextTag) {
    			((MyGenericTextTag)w).removeStyleName("is_selected");
    		}
    	}
    }
    
    /** TODO: should be in presenter!
     * 
     * @param page
     */
    private void setPageNumber(int page) {
        
        if(page < this.info.getMinPageNumber() || page > this.info.getMaxPageNumber() ) {
        	showPageError();
        }
        else {
        	bookMessage.setInnerHTML("");
        	book.setPage(Integer.parseInt(pageNumber.getValue()));
        	
        	HmMobilePersistedPropertiesManager.setLastBookPlace(book, page);
            
        	pageNumber.setValue("" + page);
        	book.setPage(page);
        	presenter.getProblemNumbers(book, book.getPage());
        }
    }
    
    private void showPageError() {
    	bookMessage.setInnerHTML(
    			"Page not found.  Only pages " + this.info.getMinPageNumber() +
    			" through " + info.getMaxPageNumber() + " included.");
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
    			showPageError();
    			pageNumber.setText(book.getPage() + "");
    		}
    	}

    }
    
    @UiHandler("getNextProblems")
    public void doGetNextProblems(ClickEvent ce) {
    	if(book.getPage() + 1 > info.getMaxPageNumber()) {
    		bookMessage.setInnerHTML("Last page");
    	}
    	else {
    		setPageNumber(book.getPage()+1);
    	}
    }
    
    @UiHandler("getPrevProblems")
    public void doGetPrevProblems(ClickEvent ce) {
    	if(book.getPage() - 1 > info.getMinPageNumber()) {
    		setPageNumber(book.getPage()-1);
    	}
    	else { 
    		bookMessage.setInnerHTML("First page");
    	}
    }

    
    class MyGenericTextTag extends GenericTextTag<String> {
        ProblemNumber pr;
        public MyGenericTextTag(ProblemNumber pr,TouchClickHandler<String> touchHandler) {
            super("li");
            addStyleName("group");
            addHandler(touchHandler);
            getElement().setInnerHTML("<span>" + pr.getProblem() + "</span>");
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

        if(problems.size() == 0) {
        	messageText.setInnerHTML("No problems found on page '" + pageNumber.getValue() + "'.");
        	messageDiv.getElement().setAttribute("style", "display: block");
        	problemNumberDiv.getElement().setAttribute("style", "display: none");
        }
        else {
        	
        	int page = Integer.parseInt(pageNumber.getValue());

        	
            String pageNumsTitle=(problems.size()==1?"problem":"problems");
            pageNumsTitle = problems.size() + " " + pageNumsTitle + " on pages: " + (page-1) + "-" + (page + 1);
            probNumsTitle.setInnerHTML(pageNumsTitle);
            
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
        return "<<";
    }
    
    @Override
    public BackAction getBackAction() {
    	BackAction ba = new BackAction() {
    		@Override
    		public void goBack() {
    			HmMobile.__clientFactory.getEventBus().fireEvent(new BackDiscoveryEvent((IPage)BookViewImpl.this));
    		}
    	};
    	
    	return ba;
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
        return "Page & Problem";
    }
    

    @UiField
    ImageElement bookImage;

    @UiField
    SpanElement minPage, maxPage, title, author;

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
    
    @UiField
    DivElement bookMessage;
    
    @UiField
    DivElement probNumsTitle;
    
    @UiField 
    Button getPrevProblems;

    @UiField
    Button getNextProblems;
}


