package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ControlPanel;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.BackPageLoadedEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackPageLoadedEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.EnableDisplayZoomEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEventHandler;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;
import hotmath.gwt.cm_rpc.client.model.ProblemNumber;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.hm_mobile.client.event.EnableDisplayZoomEvent;
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
import hotmath.gwt.hm_mobile.client.model.BookModel;
import hotmath.gwt.hm_mobile.client.model.CategoryModel;
import hotmath.gwt.hm_mobile.client.view.BookListView;
import hotmath.gwt.hm_mobile.client.view.BookListViewImpl;
import hotmath.gwt.hm_mobile.client.view.BookView;
import hotmath.gwt.hm_mobile.client.view.CategoryListViewImpl;
import hotmath.gwt.hm_mobile.client.view.TutorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provide minimal Hotmath for mobile access.
 * 
 * @author casey
 * 
 */
public class HmMobile implements EntryPoint, OrientationChangedHandler {
    
    static ForcedSubject __forcedSubject = new ForcedSubject(); 

    static {
        setupServices();
    } 
    
    
    RootPanel _rootPanel;
    RootPanel _loadingDiv;

    final static public ClientFactory __clientFactory = GWT.create(ClientFactory.class);

    public static HmMobile __instance;
    
    public void onModuleLoad() {
        
		 /*
		 * Install an UncaughtExceptionHandler which will produce <code>FATAL</code> log messages
		 */
		Log.setUncaughtExceptionHandler();
		
		// use deferred command to catch initialization exceptions in onModuleLoad2
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		  @Override
		  public void execute() {
		    onModuleLoad2();
		  }
		});        	
    }
    
    public void onModuleLoad2() {
    	
    	long startTimeMillis=0;
    	 /*
         * Use a <code>if (Log.isDebugEnabled()) {...}</code> guard to ensure that
         * <code>System.currentTimeMillis()</code> is compiled out when <code>log_level=OFF</code>, or
         * any <code>log_level</code> higher than <code>DEBUG</code>.
         */
        if (Log.isDebugEnabled()) {
          startTimeMillis = System.currentTimeMillis();
        }
        
        /*
         * Again, we need a guard here, otherwise <code>log_level=OFF</code> would still produce the
         * following useless JavaScript: <pre> var durationSeconds, endTimeMillis; endTimeMillis =
         * currentTimeMillis_0(); durationSeconds = (endTimeMillis - this$static.startTimeMillis) /
         * 1000.0; </pre>
         */
        if (Log.isDebugEnabled()) {
          long endTimeMillis = System.currentTimeMillis();
          float durationSeconds = (endTimeMillis - startTimeMillis) / 1000F;
          Log.debug("Duration: " + durationSeconds + " seconds");
        }
    	
        __instance = this;
        _loadingDiv = RootPanel.get("loading");
        _rootPanel = RootPanel.get("main-content");
        
        try {
        	Controller.installEventBus(__clientFactory.getEventBus());
            Screen screen = new Screen();
            screen.addHandler(this);
            orientationChanged(screen.getScreenOrientation());
    
            setupGlobalEventHandlers();
    
            History.addValueChangeHandler(new HmMobileHistoryListener());

            if(__forcedSubject.isForcedSubject()) {
                
                _rootPanel.add(createApplicationPanel());
                _rootPanel.getElement().getStyle().setProperty("display", "inline");

                if(__forcedSubject.isOnlyOne()) {
                    /** disable back, this is new home
                     * 
                     */
                    ((BookListViewImpl)HmMobile.__clientFactory.getBookListView()).setBackButtonText(null);
                    ((BookListViewImpl)HmMobile.__clientFactory.getBookListView()).setTitle(__forcedSubject.getTitle());
                }
                else {
                    ((CategoryListViewImpl)HmMobile.__clientFactory.getCategoryListView()).setTitle(__forcedSubject.getTitle());
                }
                
                History.fireCurrentHistoryState();
                
                __clientFactory.getEventBus().fireEvent(new EnableDisplayZoomEvent(false));
                
                if(!InitialMessage.hasBeenSeen()) {
                    new InitialMessage().showCentered();
                }
            }
            else {
                _rootPanel.add(createApplicationPanel());
                History.fireCurrentHistoryState();
            }
            
            __clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
            
            
        }
        catch(Exception e) {
            e.printStackTrace();
            Window.alert("Error during startup: " + e.getMessage());
        }
        
        
        Log.info("Hotmath Mobile Initialized");

    }
    

    private Widget createApplicationPanel() {
        /**
         * we want this to have an absolute size and be added as the top
         * component.
         * 
         */
        HeaderPanel headerPanel = new HeaderPanel();
        PagesContainerPanel pagesPanel = __clientFactory.getPagesContainer();

        FlowPanel fp = new FlowPanel();
        fp.setStyleName("app-panel");

        fp.add(headerPanel);
        fp.add(pagesPanel.getPanel());

        _pageStack = new ObservableStack<IPage>();
        pagesPanel.bind(_pageStack);
        headerPanel.bind(_pageStack);
        Controller.init(_pageStack);

        return fp;
    }


    /** TODO: Move to a central Controller 
     * 
     */
    private void setupGlobalEventHandlers() {
        final EventBus eb = __clientFactory.getEventBus();

        /** Event to provide UI info about server activity
         * 
         */
        eb.addHandler(SystemIsBusyEvent.TYPE, new SystemIsBusyEventHandler() {
			@Override
			public void showIsBusy(boolean trueFalse) {
				if(_loadingDiv != null) {
					if(trueFalse) {
						_loadingDiv.getElement().setAttribute("style", "display:block");
					}
					else {
						showLoadingMessage(_loadingDiv.getElement(), false);
						_loadingDiv.getElement().setAttribute("style", "display:none");
					}
				}
			}
			
		    private native void showLoadingMessage(Element el, boolean doShow) /*-{
		        el.style.display = doShow?'block':'none';
		    }-*/;				
			
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
        
        
        eb.addHandler(EnableDisplayZoomEvent.TYPE, new EnableDisplayZoomEventHandler() {
        	@Override
        	public void enableZoom(boolean trueFalse) {		
				RootPanel rp = RootPanel.get("head_view");
				if(rp != null) {
					if(trueFalse) {
						rp.getElement().setAttribute("content", "width=device-width, user_scalable = yes, initial-scale = 1");
					}
					else {
						rp.getElement().setAttribute("content", "width=device-width, user_scalable = no");
					}

				}
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
            	Log.info("LoadNewPageEvent fire: " + page.getClass().getName());
            	int currentScrollPos = CatchupMathMobileShared.resetViewPort();
            	IPage currentPage = _pageStack.getCount()>0?_pageStack.peek():null;
            	if(currentPage != null) {
            		pageScroll.put(currentPage,  new Integer(currentScrollPos));
            	}
                _pageStack.push(page);
                
//                if(!(page instanceof TutorViewImpl)) {
//                	eb.fireEvent(new BackPageLoadedEvent(page));
//                }
            }
        });
        
        eb.addHandler(BackPageLoadedEvent.TYPE, new BackPageLoadedEventHandler() {
        	@Override
        	public void movedBack(final IPage page) {
        		if(true)
        			return;
        		
            	Log.info("LoadNewPageEvent fire: " + page.getClass().getName());
            	if(pageScroll.containsKey(page)) {
            		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							setViewScrollPosition(pageScroll.get(page));						}
					});
            		
            	}
            }
        });        
        
    }
    
    
    private native void setViewScrollPosition(int position) /*-{
       $wnd.scrollTo(position, position);
    }-*/;
    
    
    public Map<IPage,Integer> pageScroll = new HashMap<IPage,Integer>();     
    
    
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

    
    public ForcedSubject getForcedSubject() {
        return __forcedSubject;
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


 
class HmMobileNotAvailableDialog extends SimplePanel {
   public HmMobileNotAvailableDialog() {
       PopupPanel popupPanel = new PopupPanel(false,  true);
       popupPanel.setSize("540px", "380px");

       String html = "<h1>The free beta is now closed.</h1>" +
                     "<p>Thank you for participating in our free beta of Hotmath Mobile.</p> " +
                     "<p>We will soon have mobile apps for Hotmath.  Or, you will be able to use passwords available at Hotmath.com.</p> " +
                     "<p>Check back soon for more information.</p>" +
                     "Thank you!</p> ";
       
       html = "<div style='margin: 10px;'>" + html + "</div>";
       setWidget(new HTML(html));
   }
    
}
