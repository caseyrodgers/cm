package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.history.CmHistoryQueue;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.data.PrescriptionData;
import hotmath.gwt.cm_tools.client.data.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmGuiDefinition;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewer;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionCmGuiDefinition implements CmGuiDefinition {
    
    
    /** Mark the pid as being viewed called from external JS
     * 
     * called from CatchupMath.js 
     * 
     * 
     * @TODO: remove access to globals
     * 
     * @param pid
     */
    static public void solutionHasBeenViewed_Gwt(String eventName) {
        EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_SOLUTIONS_COMPLETE,__last_solution_item));
    }
    
    /** This solution is being viewed. 
     *  The first step of solution has been requested
     *  
     *  Show info message about show work on first solution view per session
     *  
     * @param pid
     */
    static public void solutionIsBeingViewed_Gwt(String pid) {
        if(!CatchupMath.__hasBeenInformedAboutShowWork) {
            CatchupMathTools.showAlert("First try your best to answer this problem, showing the steps, by pressing the Show-Work link.");
            CatchupMath.__hasBeenInformedAboutShowWork=true;
        }
    }
    
    static private native void publishNative() /*-{
        $wnd.solutionHasBeenViewed_Gwt = @hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition::solutionHasBeenViewed_Gwt(Ljava/lang/String;);
        $wnd.solutionIsBeingViewed_Gwt = @hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition::solutionIsBeingViewed_Gwt(Ljava/lang/String;);
    }-*/;    
    
    static {
        publishNative();
        
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                __last_solution_item = (InmhItemData)event.getEventData();
            }
            
            @Override
            public String[] getEventsOfInterest() {
                // TODO Auto-generated method stub
                String types[] = {EventBus.EVENT_TYPE_SOLUTION_SHOW};
                return types;
            }
        });
    }
    
    
    

    PrescriptionContext context;
    boolean isReady;
    static public PrescriptionCmGuiDefinition __instance;
    static public InmhItemData __last_solution_item;
    PrescriptionResourcePanel _guiWidget;

    /**
     * Create and read Prescription data for the current user.
     * 
     */
    public PrescriptionCmGuiDefinition() {
        __instance = this;
        context = new PrescriptionContext(this);
        
 
        
        /** Listen for Resource Viewer closing events and make 
         *  sure the accordion resource lists are unselected.
         */
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if(event.getEventName().equals(EventBus.EVENT_TYPE_RESOURCE_VIEWER_CLOSE)) {
                    //
                }
                else if(event.getEventName().equals(EventBus.EVENT_TYPE_SOLUTIONS_COMPLETE)) {
                    // update the InmhItemData associated with the currently active solution
                    
                    InmhItemData id = ((InmhItemData)event.getEventData());
                    markResourceAsViewed(id);
                }
                else if(event.getEventName().equals(EventBus.EVENT_TYPE_RESOURCE_VIEWER_OPEN)) {
                    // if not a required problem, then set as viewed
                    ResourceViewer viewer = (ResourceViewer)event.getEventData();
                    
                    /** practice problems only get marked viewed, 
                     *  once they are fully complete (event EVENT_TYPE_SOLUTIONS_COMPLETE)
                     */
                    if(CmShared.getQueryParameter("debug") != null || !viewer.getResourceItem().getType().equals("practice"))
                        markResourceAsViewed(viewer.getResourceItem());
                }
                
            }
        });        
        
    }


  public void markResourceAsViewed(final InmhItemData resourceItem) {
        if(UserInfo.getInstance().getRunId() == 0)
            Log.error("PrescriptionCmGuiDefinition: run_id is null!");
        
        /**
         * 
         * mark this INMH resource item as being viewed
         * 
         */
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.setInmhItemAsViewed(UserInfo.getInstance().getRunId(), resourceItem.getType(), resourceItem.getFile(),
                new AsyncCallback() {

                    public void onSuccess(Object result) {
                        
                        Log.debug("PrescriptionResourceAccord: setItemAsViewed: " + resourceItem);
                        
                        boolean isSolutionResource = (resourceItem.getType().equals("practice") || resourceItem.getType().equals("cmextra"));
                        
                        // update the total count in the Header
                        // only if a practice or cmextra type
                        int vc = UserInfo.getInstance().getViewCount();
                        if(isSolutionResource)
                            vc++;
                        
                        UserInfo.getInstance().setViewCount(vc);
                        HeaderPanel.__instance.setLoginInfo();
                        
                        
                        // only mark practice problems
                        // 
                        if(!isSolutionResource)
                            return;
                        
                        resourceItem.setViewed(true);
                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                });
    }
  
    
    public CmContext getContext() {
        return context;
    }


    LayoutContainer _main;
    public Widget getWestWidget() {
        CatchupMathTools.setBusy(true);

        _main = new LayoutContainer();
        _main.setLayout(new FitLayout());
        
        _guiWidget = new PrescriptionResourcePanel();
        _main.add(_guiWidget, new BorderLayoutData(LayoutRegion.CENTER,.75f));
        // get the data for the prescription from the database

        getAsyncDataFromServer(UserInfo.getInstance().getSessionNumber());
        
        _main.add(new PrescriptionInfoPanel(PrescriptionCmGuiDefinition.__instance), new BorderLayoutData(LayoutRegion.SOUTH, .30f));

        _main.layout();
        return _main;
    }


    /**
     * Read data from server and build UI when complete
     * 
     */
    public void getAsyncDataFromServer(int sessionNumber) {

        
        // check for a pending History CmLocation change
        final CmLocation location = CmHistoryQueue.getInstance().popLocation();
        if(location != null) {
            if(location.getLocationType() == LocationType.PRESCRIPTION)
                sessionNumber = location.getLocationNumber();
        }
        
        
        // If we are currently on requested session, no need for server call
        if(isReady && UserInfo.getInstance().getSessionNumber() == sessionNumber) {
            // update the request resource viewed
            setLocation(location);
            return;
        }
        
        
        final int sessionNumber2 = sessionNumber;
        
        // clear any existing resource
        CmMainPanel.__lastInstance._mainContent.removeResource();
        CmMainPanel.__lastInstance._mainContent.layout();

        
        Log.info("PrescriptionCmGuiDefinition.getAsyncDataFromServer:" + sessionNumber + ", " + location);
        // call server process to get session data as JSON string
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        boolean updateActive = UserInfo.getInstance().isActiveUser();        
        s.execute(new GetPrescriptionAction(UserInfo.getInstance().getRunId(), sessionNumber, updateActive),new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData rdata) {
                if(rdata == null) {
                    CatchupMathTools.showAlert("There was a problem reading this prescription data");
                    return;
                }
                try {
                    
                    UserInfo.getInstance().setSessionNumber(sessionNumber2);
                    
                    int correctPercent = rdata.getDataAsInt("correct_percent");
                    UserInfo.getInstance().setCorrectPercent(correctPercent);
                    if(correctPercent == 100) {
                        getContext().doNext();
                        return;
                    }

                    String json = rdata.getDataAsString("json");
                    context.setPrescriptionData(new PrescriptionData(json));                    
                    
                    isReady = true; // signal data is ready

                    _guiWidget.buildUi(context.prescriptionData);

                    ContextController.getInstance().setCurrentContext(context);
                    

                    if(UserInfo.getInstance().isAutoTestMode()) {
                        context.runAutoTest();
                    }

                    setLocation(location);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    CatchupMathTools.setBusy(false);
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }
        });
        CatchupMathTools.setBusy(true);
    }
    
    
    static Map<String, List<InmhItemData>> _registeredResources = new HashMap<String, List<InmhItemData>>(); 
    
    /** Update the current resource loaded
     * 
     * @param location
     */
    private void setLocation(CmLocation location) {
        if(location != null) {
            String resourceTypeToView = location.getResourceType();
            if(resourceTypeToView != null) {
                
                List<InmhItemData> resourceList = _registeredResources.get(resourceTypeToView);
                if(resourceList != null) {
                    // select the Nth item
                    if(location.getResourceNumber() > -1) {
                        
                        InmhItemData itemData = resourceList.get(location.getResourceNumber());
                        CmMainPanel.__lastInstance._mainContent.showResource(itemData);
                        
                    }
                    else {
                        String resourceId = location.getResourceId();
                        InmhItemData item = new InmhItemData();
                        item.setType(resourceTypeToView);
                        item.setFile(resourceId);
                        CmMainPanel.__lastInstance._mainContent.showResource(item);
                    }
                }
            }
        }
    }

    public Widget getCenterWidget() {
        return null;
    }

    public String getTitle() {
        return "Prescription Resource";
    }
    
}


/** Defines the main accordion widget containing the resources
 * 
 * @author casey
 *
 */
class PrescriptionResourcePanel extends LayoutContainer {

    static PrescriptionResourcePanel __instance;

    PrescriptionData pdata;
    boolean isReady;
    public PrescriptionResourcePanel() {
        __instance = this;
    }

    
    /**
     * Build or rebuild the GUI from list of resource objects
     * 
     * @param resources
     */
    public void buildUi(PrescriptionData pdata) {
        List<PrescriptionSessionDataResource> resources = pdata.getCurrSession().getInmhResources();

        removeAll();
        
        setScrollMode(Scroll.AUTO);
        VerticalPanel vp = new VerticalPanel();
        setStyleName("prescription-cm-gui-definition-resource-panel");

        // setTitle("Choose a resource type, then click one of its items.");
        for (PrescriptionSessionDataResource resource : resources) {
            Button btn = new ResourceMenuButton(resource);
            
            if(resource.getItems().size() == 0)
                btn.setEnabled(false);
            
            
            vp.add(btn);
        }
       
       /** Add the standard resources
        * 
        */
       for(PrescriptionSessionDataResource type: new CmInmhStandardResources()) {
           
           Button btn = new ResourceMenuButton(type);
           vp.add(btn);
        }        

        add(vp);
        layout();
    }
    

    
    public void expandResourcePracticeProblems() {
        expandResourceType("practice");
    }
    
    /** Expand the resource node exposing resource items
     * 
     * Just matches with 'startsWith'
     * 
     * Return the ResourceList expanded, or null if no match
     * 
     * 
     * @param resourceType The table of the resource type
     * 
     */
    public void expandResourceType(String resourceType) {
        CatchupMathTools.showAlert("Show resource type: " + resourceType);
    }
}
