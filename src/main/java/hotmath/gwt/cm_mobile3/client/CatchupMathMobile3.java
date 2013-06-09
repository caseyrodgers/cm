package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity;
import hotmath.gwt.cm_mobile3.client.event.AutoAdvanceUserEvent;
import hotmath.gwt.cm_mobile3.client.event.AutoAdvanceUserEventHandlerImpl;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewEvent;
import hotmath.gwt.cm_mobile3.client.ui.HeaderPanel;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceResultsViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorViewImpl;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonViewImpl;
import hotmath.gwt.cm_mobile3.client.view.QuizViewImpl;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.HasWhiteboard;
import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.activity.AutoCreateActivity;
import hotmath.gwt.cm_mobile_shared.client.activity.ParallelProgramActivity;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.BackPageLoadedEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackPageLoadedEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutHandler;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.util.LoadingDialog;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;
import hotmath.gwt.cm_mobile_shared.client.view.AutoCreateView;
import hotmath.gwt.cm_mobile_shared.client.view.AutoCreateViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.ParallelProgramView;
import hotmath.gwt.cm_mobile_shared.client.view.ParallelProgramViewImpl;
import hotmath.gwt.cm_mobile_shared.client.view.PrescriptionLessonResourceVideoView;
import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkView;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class CatchupMathMobile3 implements EntryPoint, OrientationChangedHandler {

    public static CatchupMathMobile3 __instance;

    ObservableStack<IPage> _pageStack = new ObservableStack<IPage>();
    public Map<IPage, Integer> pageScroll = new HashMap<IPage, Integer>();
    RootPanel _rootPanel;

    final static public ClientFactory __clientFactory = GWT.create(ClientFactory.class);
    FormLoaderListeners formLoaders = new FormLoaderListenersImplHistory();

    public CatchupMathMobile3() {
        /*
         * Install an UncaughtExceptionHandler which will produce
         * <code>FATAL</code> log messages
         */
        Log.setUncaughtExceptionHandler();

        /**
         * register the main Flow listener that handle the transitions from one
         * CM program flow to the next.
         */
        __clientFactory.getEventBus().addHandler(HandleNextFlowEvent.TYPE, new HandleNextFlowEventHandlerImpl(__clientFactory.getEventBus()));
    }

    /**
     * Note, we defer all application initialization code to
     * {@link #onModuleLoadAux()} so that the UncaughtExceptionHandler can catch
     * any unexpected exceptions.
     */
    @Override
    public void onModuleLoad() {
        __instance = this;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoadAux();
            }
        });
    }

    LoadingDialog loadingDialog = null;

    private void onModuleLoadAux() {

        long startTimeMillis = 0;
        /*
         * Use a <code>if (Log.isDebugEnabled()) {...}</code> guard to ensure
         * that <code>System.currentTimeMillis()</code> is compiled out when
         * <code>log_level=OFF</code>, or any <code>log_level</code> higher than
         * <code>DEBUG</code>.
         */
        if (Log.isDebugEnabled()) {
            startTimeMillis = System.currentTimeMillis();
            registerDebugginExternalJs();

        }

        /*
         * Again, we need a guard here, otherwise <code>log_level=OFF</code>
         * would still produce the following useless JavaScript: <pre> var
         * durationSeconds, endTimeMillis; endTimeMillis =
         * currentTimeMillis_0(); durationSeconds = (endTimeMillis -
         * this$static.startTimeMillis) / 1000.0; </pre>
         */
        if (Log.isDebugEnabled()) {
            long endTimeMillis = System.currentTimeMillis();
            float durationSeconds = (endTimeMillis - startTimeMillis) / 1000F;
            Log.debug("Duration: " + durationSeconds + " seconds");
        }

        __instance = this;
        _loadingDiv = RootPanel.get("loading");
        _loadingDiv.getElement().setAttribute("style", "display:none");

        new LoadingDialog(__clientFactory.getEventBus());
        _rootPanel = RootPanel.get("main-content");

        try {

            if (false && CatchupMathMobileShared.getQueryParameter("debug") == null) {
                /** only allow IPad access for now */
                String userAgent = getUserAgent();
                // Window.alert("User Agent: " + userAgent);

                String ualc = userAgent.toLowerCase();
                if (ualc.indexOf("ipad") == -1 && ualc.indexOf("android") == -1 && ualc.indexOf("iphone") == -1) {
                    Location.assign("/cm_mobile_not_supported.html");
                    // _rootPanel.add(new BrowserNotSupportedPanel());
                    return;
                }
            }

            Controller.installEventBus(__clientFactory.getEventBus());
            _rootPanel.add(createApplicationPanel());

            formLoaders.setupListeners(__clientFactory.getEventBus());

            /**
             * allow each display panel to flow left-to-right.
             * 
             */
            _rootPanel.getElement().getStyle().setProperty("display", "inline");

            Screen screen = new Screen();
            screen.addHandler(this);
            orientationChanged(screen.getScreenOrientation());

            setupGlobalEventHandlers(__clientFactory.getEventBus());

            History.addValueChangeHandler(new CatchupMathMobileHistoryListener());

            String suid = CmGwtUtils.getQueryParameter("uid");
            int uid = 0;
            if (suid != null) {
                uid = Integer.parseInt(suid);
                SharedData.saveUidToLocalStorage(uid);
            }

            if (!loadFirstPanelMaybe(uid)) {
                History.fireCurrentHistoryState();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("Error during startup: " + e.getMessage());
        } finally {
            __clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
        }

        __clientFactory.getEventBus().addHandler(AutoAdvanceUserEvent.TYPE, new AutoAdvanceUserEventHandlerImpl());

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        });

        Log.info("Catchup Math Mobile Initialized");
    }

    private boolean loadFirstPanelMaybe(int uid) {
        boolean handled=false;
        try {
            String type = CmGwtUtils.getQueryParameter("type");
            if(type == null) {
                type = "";
            }
            
            if (type.equals("AUTO_CREATE")) {
                
                Log.info("Showing Auto Create login");
                /** show auto create view */
                AutoCreateActivity activity = new AutoCreateActivity(uid);
                final AutoCreateView view = new AutoCreateViewImpl();
                view.setPresenter(activity, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        CmRpcCore.EVENT_BUS.fireEvent(new LoadNewPageEvent(view));
                    }
                });
                handled = true;
            } else if(type.equals("PARALLEL_PROGRAM")) {
                Log.info("Showing Parallel Program login");
                ParallelProgramActivity activity = new ParallelProgramActivity(uid);
                final ParallelProgramView view = new ParallelProgramViewImpl();
                view.setPresenter(activity, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        CmRpcCore.EVENT_BUS.fireEvent(new LoadNewPageEvent(view));
                    }
                });
                handled = true;
            } else if (uid > 0) {
                SharedData.saveUidToLocalStorage(uid);
                
                SharedData.makeSureUserHasBeenRead(new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        CmRpcCore.EVENT_BUS.fireEvent(new HandleNextFlowEvent(SharedData.getMobileUser().getFlowAction()));
                    }
                });
                handled = true;
            }

        } catch (Exception e) {
            MessageBox.showError("Error reading login information: " + e);
        }
        
        return handled;
    }

    RootPanel _loadingDiv;

    native private String getUserAgent() /*-{
                                         if(typeof navigator !== 'undefined') {
                                         return navigator.userAgent;
                                         }
                                         else {
                                         return 'unknown';
                                         }
                                         }-*/;

    @Override
    public void orientationChanged(ScreenOrientation newOrientation) {
        if (newOrientation == ScreenOrientation.Portrait) {
            _rootPanel.removeStyleName("landscape");
            _rootPanel.addStyleName("portrait");
        } else {
            _rootPanel.addStyleName("landscape");
            _rootPanel.removeStyleName("portrait");
        }

        CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
    }

    /**
     * TODO: Move to a central Controller
     * 
     */
    public void setupGlobalEventHandlers(EventBus eventBus) {
        final EventBus eb = eventBus;

        /**
         * Provide central place to insert into IPage system
         * 
         */
        eb.addHandler(LoadNewPageEvent.TYPE, new LoadNewPageEventHandler() {
            @Override
            public void loadPage(IPage page) {
                try {
                    Log.info("LoadNewPageEvent fire: " + page.getViewTitle());
                    int currentScrollPos = CatchupMathMobileShared.resetViewPort();
                    IPage currentPage = _pageStack.getCount() > 0 ? _pageStack.peek() : null;
                    if (currentPage != null) {
                        pageScroll.put(currentPage, new Integer(currentScrollPos));
                    }
                    _pageStack.push(page);
                } catch (Exception e) {
                    Log.error("Error loading page", e);
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
            }
        });
        eb.addHandler(BackDiscoveryEvent.TYPE, new BackDiscoveryEventHandler() {
            @Override
            public void discoverBack(IPage last) {
                if (last != null) {
                    determineBackDestination(last);
                } else {
                    History.back();
                }
            }
        });

        /**
         * Look for special cases and handle custom for the given view.
         * 
         * TODO: This should be in the view!
         */
        eb.addHandler(BackPageLoadedEvent.TYPE, new BackPageLoadedEventHandler() {
            @Override
            public void movedBack(final IPage page) {
                Log.info("BackPageLoadedEvent fire: " + page.getClass().getName());
                /**
                 * these are app/panel specific hooks
                 * 
                 * TODO: find a more general way for specific hooks
                 */
                if (page instanceof HasWhiteboard) {
                    ShowWorkActivity.saveWhiteboard();
                }

                if (page instanceof QuizViewImpl || page instanceof PrescriptionLessonResourceResultsViewImpl) {
                    final int scrollTo = pageScroll.get(page);
                    if (scrollTo > 0) {
                        scrollWindowTo(scrollTo);
                    }
                } else if (page instanceof PrescriptionLessonResourceVideoView) {
                    eb.fireEvent(new LoadNewPageEvent(__clientFactory.getPrescriptionLessonView()));
                } else if (page instanceof PrescriptionLessonViewImpl) {
                    final int scrollTo = pageScroll.get(page);
                    if (scrollTo > 0) {
                        scrollWindowTo(scrollTo);
                    }
                } else if (page instanceof PrescriptionLessonResourceTutorViewImpl) {
                    final int scrollTo = pageScroll.get(page);
                    if (scrollTo > 0) {
                        scrollWindowTo(scrollTo);
                    }
                    // eb.fireEvent(new
                    // LoadNewPageEvent(__clientFactory.getPrescriptionLessonView()));
                }
            }
        });

        eb.addHandler(UserLogoutEvent.TYPE, new UserLogoutHandler() {
            @Override
            public void userLogout() {
                // CmRpcCore.EVENT_BUS.fireEvent(new ShowLoginViewEvent());
                Window.Location.replace("/index.html");
            }
        });
    }

    public int getScrollPositionFor(IPage page) {
        Integer position = pageScroll.get(page);
        if (position != null) {
            return position;
        } else {
            return 0;
        }
    }

    /**
     * Look at current page and try to determine the proper thing to do for the
     * 'back' action.
     * 
     * @param page
     */
    private void determineBackDestination(IPage page) {
        if (page instanceof WelcomeView) {
            __clientFactory.getEventBus().fireEvent(new ShowLoginViewEvent());
        } else if (page instanceof ShowWorkView) {
            ShowWorkActivity.disconnectWhiteboard();
        } else {
            History.back();
        }
    }

    native private void scrollWindowTo(int position) /*-{
                                                     try {
                                                     setTimeout(function() {
                                                     $wnd.scrollTo(0,position);
                                                     },0);
                                                     }
                                                     catch(e) {
                                                     alert(e);
                                                     }
                                                     }-*/;

    private Widget createApplicationPanel() {
        /**
         * we want this to have an absolute size and be added as the top
         * component.
         * 
         */
        HeaderPanel headerPanel = new HeaderPanel(__clientFactory.getEventBus());
        PagesContainerPanel pagesPanel = __clientFactory.getPagesContainer();

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.setStyleName("app-panel");

        mainPanel.add(headerPanel);
        mainPanel.add(pagesPanel.getPanel());

        _pageStack = new ObservableStack<IPage>();
        pagesPanel.bind(_pageStack);
        headerPanel.bind(_pageStack);
        Controller.init(_pageStack);

        return mainPanel;
    }

    static private void debugLogOut(String message) {
        Log.debug(message);
    }

    /**
     * Override the debug output to provide tunnel to the GWT Log interface
     * 
     */
    private native void registerDebugginExternalJs() /*-{
                                                     $wnd.debug = function(x){
                                                     @hotmath.gwt.cm_mobile3.client.CatchupMathMobile3::debugLogOut(Ljava/lang/String;)(x);
                                                     };      
                                                     }-*/;

    private void doit(String cmJson) {
        if (cmJson != null) {
            JSONValue jsonValue = JSONParser.parse(cmJson);
            JSONObject o = jsonValue.isObject();
            String cmStartType = o.get("type").isString().stringValue();
            if (cmStartType != null) {
                if (cmStartType.equals("AUTO_CREATE")) {
                    MessageBox.showError("Auto Create!");
                }
            }
        }
    }

    // private native void registerExternalJs() /*-{
    // $wnd.gwt_fireBrowserResizedEvent = function(){
    // return
    // @hotmath.gwt.cm_mobile3.client.CatchupMathMobile3::gwt_fireBrowserResizedEvent()();
    // };
    // }-*/;
}
