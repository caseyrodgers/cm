package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ControlPanel;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEventHandler;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.hm_mobile.client.event.GoBackEvent;
import hotmath.gwt.hm_mobile.client.event.GoBackEventHandler;
import hotmath.gwt.hm_mobile.client.event.LoadNewPageEvent;
import hotmath.gwt.hm_mobile.client.event.LoadNewPageEventHandler;
import hotmath.gwt.hm_mobile.client.event.ShowBookListEvent;
import hotmath.gwt.hm_mobile.client.event.ShowBookListEventHandler;
import hotmath.gwt.hm_mobile.client.event.ShowBookSearchEvent;
import hotmath.gwt.hm_mobile.client.event.ShowBookSearchEventHandler;
import hotmath.gwt.hm_mobile.client.event.ShowBookViewEvent;
import hotmath.gwt.hm_mobile.client.event.ShowBookViewEventHandler;
import hotmath.gwt.hm_mobile.client.event.ShowCategoryListEvent;
import hotmath.gwt.hm_mobile.client.event.ShowCategoryListEventHandler;
import hotmath.gwt.hm_mobile.client.event.ShowHelpEvent;
import hotmath.gwt.hm_mobile.client.event.ShowHelpEventHandler;
import hotmath.gwt.hm_mobile.client.event.ShowHomeViewEvent;
import hotmath.gwt.hm_mobile.client.event.ShowHomeViewEventHandler;
import hotmath.gwt.hm_mobile.client.event.ShowTutorViewEvent;
import hotmath.gwt.hm_mobile.client.event.ShowTutorViewEventHandler;
import hotmath.gwt.hm_mobile.client.event.SystemIsBusyEvent;
import hotmath.gwt.hm_mobile.client.event.SystemIsBusyEventHandler;
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.model.ProblemNumber;
import hotmath.gwt.hm_mobile.client.place.CategoryListPlace;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.BookView;
import hotmath.gwt.hm_mobile.client.view.TutorView;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provide minimal CM for mobile access.
 * 
 * @author casey
 * 
 */
public class HmMobile implements EntryPoint, OrientationChangedHandler {
    
    static {
        setupServices();
    } 
    
    
    RootPanel _rootPanel;

    final static public ClientFactory __clientFactory = GWT.create(ClientFactory.class);

    Place defaultPlace = new CategoryListPlace("");
    ControlPanel _controlPanel;

    public static HmMobile __instance;
    
    public void onModuleLoad() {
        __instance = this;
        _rootPanel = RootPanel.get("main-content");
        try {
            
        	Controller.installEventBus(__clientFactory.getEventBus());
            
            GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                @Override
                public void onUncaughtException(Throwable e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            });
            
            _controlPanel = new MyControlPanel();
            _rootPanel.add(_controlPanel);     
            _rootPanel.add(createApplicationPanel());
    
            _rootPanel.getElement().getStyle().setProperty("display", "inline");
    
            Screen screen = new Screen();
            screen.addHandler(this);
            orientationChanged(screen.getScreenOrientation());
    
            setupGlobalEventHandlers();
    
            History.addValueChangeHandler(new HmMobileHistoryListener());

            initializeExternalJs();
            
            /** just once */
            CatchupMathMobileShared.__instance.hideBusyPanel();
            
            History.fireCurrentHistoryState();
        }
        catch(Exception e) {
            e.printStackTrace();
            Window.alert("Error during startup: " + e.getMessage());
        }

    }

    class MyAcceptsOneWidget extends SimplePanel {

    }

    private Widget createApplicationPanel() {
        /**
         * we want this to have an absolute size and be added as the top
         * component.
         * 
         */
        HeaderPanel headerPanel = new HeaderPanel();
        PagesContainerPanel pagesPanel = new PagesContainerPanel();

        FlowPanel fp = new FlowPanel();
        fp.setStyleName("app-panel");

        fp.add(headerPanel);
        fp.add(pagesPanel);

        _pageStack = new ObservableStack<IPage>();
        pagesPanel.bind(_pageStack);
        headerPanel.bind(_pageStack);
        Controller.init(_pageStack);

        return fp;
    }

    private void setupGlobalEventHandlers() {
        EventBus eb = __clientFactory.getEventBus();

        /** Event to provide UI info about server activity
         * 
         */
        eb.addHandler(SystemIsBusyEvent.TYPE, new SystemIsBusyEventHandler() {
			@Override
			public void showIsBusy(boolean trueFalse) {
				_controlPanel.showBusy(trueFalse);
			}
        });

        
        /** events to handle mapping to GWT history listener
         * 
         */
        eb.addHandler(ShowTutorViewEvent.TYPE, new ShowTutorViewEventHandler() {
            @Override
            public void showTutor(ProblemNumber problem) {
                History.newItem("TutorViewPlace:" + problem.getPid() + uniq());
            }
        });
        eb.addHandler(ShowBookViewEvent.TYPE, new ShowBookViewEventHandler() {
            @Override
            public void showBook(BookModel book) {
                History.newItem("BookViewPlace:" + book.getTextCode() + uniq());
            }
        });
        
        eb.addHandler(ShowCategoryListEvent.TYPE, new ShowCategoryListEventHandler() {
            @Override
            public void showCategoryList() {
                History.newItem("CategoryListPlace"  + uniq());
            }
        });

        eb.addHandler(ShowBookListEvent.TYPE, new ShowBookListEventHandler() {
            @Override
            public void showBookList(CategoryModel category) {
                History.newItem("BookListPlace:" + category.getCategory() + uniq());
            }
        });
        
        eb.addHandler(ShowHomeViewEvent.TYPE, new ShowHomeViewEventHandler() {
            @Override
            public void showHome() {
                History.newItem("HomeViewPlace" + uniq());
            }
        });
        
        eb.addHandler(ShowBookSearchEvent.TYPE,new ShowBookSearchEventHandler() {
			@Override
			public void showBookSearch() {
				History.newItem("BookSearchPlace" + uniq());
			}
        });
        
        eb.addHandler(ShowHelpEvent.TYPE,new ShowHelpEventHandler() {
			@Override
			public void showHelp() {
				History.newItem("HelpPlace" + uniq());
			}
		});
        
        
        
        eb.addHandler(BackDiscoveryEvent.TYPE,new BackDiscoveryEventHandler() {
			@Override
			public void discoverBack(IPage last) {
				if(last != null) {
					determineBackDestination(last);
				}
				else {
					History.back();
				}
			}
		});

        
        /** Provide central place to insert into IPage system 
         * 
         */
        eb.addHandler(LoadNewPageEvent.TYPE, new LoadNewPageEventHandler() {
            @Override
            public void loadPage(IPage page) {
                _pageStack.push(page);
            }
        });
        
    }
    
    
    
    
    /** Look at current page and try to determine the proper thing 
     *   to do for the 'back' action.
     *   
     * @param page
     */
    private void determineBackDestination(IPage page) {
    	if(page instanceof TutorView) {
    		TutorView view = (TutorView)page;
    		ProblemNumber pn = view.getLoadedProblem();
    		String textCode = pn.getTextCode(); 
    		__clientFactory.getEventBus().fireEvent(new ShowBookViewEvent(new BookModel(textCode)));
    	}
    	else if(page instanceof BookView) {
    		BookView view = (BookView)page;
    		BookModel book = view.getLoadedBookModel();
    		__clientFactory.getEventBus().fireEvent(new ShowBookListEvent(new CategoryModel(book.getSubject())));
    	}
    	else if(page instanceof BookListView) {
    		__clientFactory.getEventBus().fireEvent(new ShowCategoryListEvent());
    	}
    	else {
    		History.back();
    	}
    }
    
    
    
    private String uniq() {
    	return ":" + System.currentTimeMillis();
    }

    /**
     * call global JS function to intialize any external resources
     * 
     */
    private native void initializeExternalJs()/*-{
                                              $wnd.initializeExternalJs();
                                              }-*/;

    ObservableStack<IPage> _pageStack;

    @Override
    public void orientationChanged(ScreenOrientation newOrientation) {
        if (newOrientation == ScreenOrientation.Portrait) {
            _rootPanel.removeStyleName("landscape");
            _rootPanel.addStyleName("portrait");
        } else {
            _rootPanel.addStyleName("landscape");
            _rootPanel.removeStyleName("portrait");
        }
    }
    
    
    /** 
     *  Get the single CmServiceAsync instance to all
     *  for sending RPC commands.
     *  
     * @return
     */
    static CmServiceAsync _serviceInstance;
    static public CmServiceAsync getCmService() {
        return _serviceInstance;
    }
    

    static private void setupServices() {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
    }
}

class MyControlPanel extends ControlPanel {
    static List<ControlAction> __defaultList = new ArrayList<ControlAction>();
    static {
        __defaultList.add(new ControlAction("Home") {
            @Override
            public void doAction() {
                HmMobile.__clientFactory.getEventBus().fireEvent(new ShowCategoryListEvent());
            }
        });  
        __defaultList.add(new ControlAction("Back") {
            @Override
            public void doAction() {
                Controller.navigateBack();
            }
        });        

        
        __defaultList.add(new ControlAction("Text Search") {
            @Override
            public void doAction() {
            	 HmMobile.__clientFactory.getEventBus().fireEvent(new ShowBookSearchEvent());
            }
        });
        __defaultList.add(new ControlAction("Help") {
            @Override
            public void doAction() {
                HmMobile.__clientFactory.getEventBus().fireEvent(new ShowHelpEvent());
            }
        });        
    }
    
    public MyControlPanel() {
        super(__defaultList);
    }
}
