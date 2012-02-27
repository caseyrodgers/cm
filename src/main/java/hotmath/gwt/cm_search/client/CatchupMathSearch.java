package hotmath.gwt.cm_search.client;

import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.event.DoSearchEvent;
import hotmath.gwt.cm_mobile_shared.client.event.DoSearchEventHandler;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEventHandler;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;
import hotmath.gwt.cm_search.client.activity.SearchActivity;
import hotmath.gwt.cm_search.client.places.SearchPlace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class CatchupMathSearch implements EntryPoint, OrientationChangedHandler {


    static Map<String, String> _queryParameters = null;
    static public String getQueryParameter(String name) {
        if(_queryParameters == null) {
            _queryParameters = readQueryString();
        }
        return _queryParameters.get(name);
    }
    
    private Place defaultPlace = new SearchPlace("");
    private SimplePanel appWidget = new SimplePanel();
    
    
    final static public ClientFactory __clientFactory = GWT.create(ClientFactoryImpl.class);
    static public EventBus __eventBus = __clientFactory.getEventBus();
    static {
        setupJsHooks();

    }

    static private Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        Window.Location.getHostName();
        return m;
    }
    
    

    public void onModuleLoad() {
        Log.setUncaughtExceptionHandler();
        setupGlobalListeners();
        
        // use deferred command to catch initialization exceptions in onModuleLoad2
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            onModuleLoad2();
          }
        });     
    }
    
    RootPanel _loadingDiv;
    RootPanel _rootPanel;
    
    private void setupGlobalListeners() {
        _loadingDiv = RootPanel.get("loading");

        
        
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
        
        
        eb.addHandler(DoSearchEvent.TYPE, new DoSearchEventHandler() {
            @Override
            public void doSearch(String search) {
//                SearchActivity searchActivity = new SearchActivity(new SearchPlace(search),__clientFactory);
//                __clientFactory.getSearchView().setPresenter(searchActivity);
                
                
                __clientFactory.getPlaceContainer().goTo(new SearchPlace(search));
            }
        });
                
    }
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad2() {
        
        Log.info("Starting CM Search");
        
        _rootPanel = RootPanel.get("main-content");
        
        appWidget.add(new Label("The Main Widget"));
        
        PlaceController placeController =  __clientFactory.getPlaceContainer();

        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityMapper activityMapper =  new AppActivityMapper(__clientFactory);
        ActivityManager activityManager = new ActivityManager(activityMapper, __eventBus);
        activityManager.setDisplay(appWidget);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        AppPlaceHistoryMapper historyMapper = GWT.create(AppPlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, __eventBus, defaultPlace);

        RootPanel.get("main-content").add(appWidget);
        
        // Goes to the place represented on URL else default place
        historyHandler.handleCurrentHistory();

        
        Screen screen = new Screen();
        screen.addHandler(this);
        orientationChanged(screen.getScreenOrientation());        
        
        __clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
    }

    native static private void setupJsHooks() /*-{
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
        
    }    
    
}


