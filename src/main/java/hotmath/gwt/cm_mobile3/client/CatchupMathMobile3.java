package hotmath.gwt.cm_mobile3.client;

import hotmath.gwt.cm_mobile3.client.activity.ShowWorkActivity;
import hotmath.gwt.cm_mobile3.client.event.AutoAdvanceUserEvent;
import hotmath.gwt.cm_mobile3.client.event.AutoAdvanceUserEventHandlerImpl;
import hotmath.gwt.cm_mobile3.client.event.HandleNextFlowEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewEvent;
import hotmath.gwt.cm_mobile3.client.ui.HeaderPanel;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.QuizViewImpl;
import hotmath.gwt.cm_mobile3.client.view.ShowWorkView;
import hotmath.gwt.cm_mobile3.client.view.WelcomeView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackDiscoveryEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.BackPageLoadedEvent;
import hotmath.gwt.cm_mobile_shared.client.event.BackPageLoadedEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.util.LoadingDialog;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
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

        /** register the main Flow listener that handle
         *  the transisions from one CM program flow to the next.
         */
        __clientFactory.getEventBus().addHandler(HandleNextFlowEvent.TYPE,new HandleNextFlowEventHandlerImpl(__clientFactory.getEventBus()));
    }

    @Override
    public void onModuleLoad() {

        __instance = this;

        // use deferred command to catch initialization exceptions in
        // onModuleLoad2
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoadAux();
            }
        });

    }

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
            Controller.installEventBus(__clientFactory.getEventBus());
            _rootPanel.add(createApplicationPanel());

            
            
            formLoaders.setupListeners(__clientFactory.getEventBus());
            
            /** allow each display panel to flow left-to-right. 
             * 
             */
            _rootPanel.getElement().getStyle().setProperty("display", "inline");

            Screen screen = new Screen();
            screen.addHandler(this);
            orientationChanged(screen.getScreenOrientation());

            setupGlobalEventHandlers(__clientFactory.getEventBus());

            History.addValueChangeHandler(new CatchupMathMobileHistoryListener());

            // initializeExternalJs();

            // History.fireCurrentHistoryState();
            __clientFactory.getEventBus().fireEvent(new ShowLoginViewEvent());
            if (!InitialMessage.hasBeenSeen()) {
                new InitialMessage().showCentered();
            }
            
            
//            CreateTestRunResponse testRunResponse = new CreateTestRunResponse(null,0,0,0,false);
//            testRunResponse.setTestCorrectPercent(50);
//            new QuizCheckInfoDialog(__clientFactory.getEventBus(),
//                   testRunResponse);
//            
//            QuestionBox.askYesNoQuestion("Test title", "Test question", new QuestionBox.CallBack() {
//                @Override
//                public void onSelectYes() {
//                }
//            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert("Error during startup: " + e.getMessage());
        }
        finally {
            __clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
        }
        
        __clientFactory.getEventBus().addHandler(AutoAdvanceUserEvent.TYPE, new AutoAdvanceUserEventHandlerImpl());

        Log.info("Catchup Math Mobile Initialized");

    }

    RootPanel _loadingDiv;

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
                    Log.info("LoadNewPageEvent fire: " + page.getTitle());
                    int currentScrollPos = CatchupMathMobileShared.resetViewPort();
                    IPage currentPage = _pageStack.getCount() > 0 ? _pageStack.peek() : null;
                    if (currentPage != null) {
                        pageScroll.put(currentPage, new Integer(currentScrollPos));
                    }
                    _pageStack.push(page);
                } catch (Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
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
        eb.addHandler(BackPageLoadedEvent.TYPE, new BackPageLoadedEventHandler() {
            @Override
            public void movedBack(final IPage page) {
                Log.info("BackPageLoadedEvent fire: " + page.getClass().getName());
                /** these are app/panel specific hooks
                 * 
                 * TODO: find a more general way for specific hooks
                 */
                if(page instanceof QuizViewImpl || page instanceof PrescriptionLessonResourceTutorView) {
                    ShowWorkActivity.saveWhiteboard();
                }
            }
        });        
    }

    /** Look at current page and try to determine the proper thing 
     *   to do for the 'back' action.
     *   
     * @param page
     */
    private void determineBackDestination(IPage page) {
        if(page instanceof WelcomeView) {
            __clientFactory.getEventBus().fireEvent(new ShowLoginViewEvent());
        }
        else if(page instanceof ShowWorkView) {
            ShowWorkActivity.disconnectWhiteboard();
        }
        else {
            History.back();
        }
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
}
