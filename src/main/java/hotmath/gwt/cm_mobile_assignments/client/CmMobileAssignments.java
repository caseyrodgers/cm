package hotmath.gwt.cm_mobile_assignments.client;

import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;
import hotmath.gwt.cm_mobile_assignments.client.util.AssBusy;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData;
import hotmath.gwt.cm_mobile_assignments.client.util.AssData.CallbackWhenDataReady;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_core.client.rpc.CmService;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.mvp.client.AnimatableDisplay;
import com.googlecode.mgwt.mvp.client.AnimatingActivityManager;
import com.googlecode.mgwt.mvp.client.history.MGWTPlaceHistoryHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort.DENSITY;


public class CmMobileAssignments implements EntryPoint {
    
    int _uid;
    
    @Override
    public void onModuleLoad() {
        ViewPort viewPort = new MGWTSettings.ViewPort();
        viewPort.setTargetDensity(DENSITY.MEDIUM);
        //viewPort.setUserScaleAble(true).setMinimumScale(1.0).setMinimumScale(1.0).setMaximumScale(1.0);

        MGWTSettings settings = new MGWTSettings();
        settings.setViewPort(viewPort);
        settings.setIconUrl("logo.png");
        settings.setAddGlosToIcon(true);
        settings.setFullscreen(true);
        settings.setPreventScrolling(true);

        MGWT.applySettings(settings);

        final ClientFactory clientFactory = new ClientFactoryImplDefault();

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        
        AssPlaceMapper historyMapper = GWT.create(AssPlaceMapper.class);

        createDisplay(clientFactory);

        AssHistoryObserver historyObserver = new AssHistoryObserver();

        final MGWTPlaceHistoryHandler historyHandler = new MGWTPlaceHistoryHandler(historyMapper, historyObserver);
        
        
        AssBusy.showBusy(true);
        AssData.readAssData(new CallbackWhenDataReady() {
            @Override
            public void isReady() {
                AssBusy.showBusy(false);
                historyHandler.register(clientFactory.getPlaceController(), clientFactory.getEventBus(), new HomePlace());
                historyHandler.handleCurrentHistory();        
            }
        });
    }
    
    

    static CmServiceAsync _cmService;
    
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
    

    static {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        
        final CmServiceAsync cmService = (CmServiceAsync)GWT.create(CmService.class);
        ((ServiceDefTarget) cmService).setServiceEntryPoint(point + "services/cmService");
        _serviceInstance = cmService;
    }
    
    private void createDisplay(ClientFactory clientFactory) {
        AnimatableDisplay display = GWT.create(AnimatableDisplay.class);

        AssActivityMapper appActivityMapper = new AssActivityMapper(clientFactory);

        AssAnimationMapper appAnimationMapper = new AssAnimationMapper();

        AnimatingActivityManager activityManager = new AnimatingActivityManager(appActivityMapper, appAnimationMapper, clientFactory.getEventBus());

        activityManager.setDisplay(display);

        RootPanel.get().add(display);        
    }

}