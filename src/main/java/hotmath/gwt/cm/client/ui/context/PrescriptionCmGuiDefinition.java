package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.history.CmHistoryQueue;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.data.PrescriptionData;
import hotmath.gwt.cm_tools.client.data.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.ui.CmGuiDefinition;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.util.GenericVideoPlayerForMona;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.rpc.action.SetInmhItemAsViewedAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.StatusImagePanel;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionCmGuiDefinition implements CmGuiDefinition {

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

    }

    /**
     * Mark resource as viewed
     * 
     * @param resourceItem
     */
    public void markResourceAsViewed(final InmhItemData resourceItem) {
        if (UserInfo.getInstance().getRunId() == 0)
            Log.error("PrescriptionCmGuiDefinition: run_id is null!");

        /**
         * 
         * mark this INMH resource item as being viewed
         * 
         */
        new RetryAction<RpcData>() {

            @Override
            public void attempt() {
                SetInmhItemAsViewedAction action = new SetInmhItemAsViewedAction(UserInfo.getInstance().getRunId(), resourceItem.getType(),resourceItem.getFile());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }
            
            public void oncapture(RpcData result) {

                Log.debug("PrescriptionResourceAccord: setItemAsViewed: " + resourceItem);

                boolean isSolutionResource = (resourceItem.getType().equals("practice") || resourceItem.getType()
                        .equals("cmextra"));

                // update the total count in the Header
                // only if a practice or cmextra type
                int vc = UserInfo.getInstance().getViewCount();
                if (isSolutionResource)
                    vc++;

                UserInfo.getInstance().setViewCount(vc);
                HeaderPanel.__instance.setLoginInfo();

                // only mark practice problems
                // 
                if (!isSolutionResource)
                    return;

                resourceItem.setViewed(true);
            }

        }.attempt();
    }

    public void updateCheckMarks() {
        _guiWidget.expandResourcePracticeProblems();
    }

    public CmContext getContext() {
        return context;
    }

    /**
     * Display the central help message in the main resource area.
     * 
     * 
     * @TODO: This has to be done in a Timer due to bug in GXT. Seems like this
     *        is getting called during a 'layout' and we cannot change things in
     *        this thread. But, by creating a new thread (Timer) the write
     *        works.
     */
    private void showHelpPanel() {

        Timer t = new Timer() {
            @Override
            public void run() {
                String html = 
                          "<div class='info'>" 
                        + "<b>Catchup Math: the more you do, the more you learn!</b>" + "<ul>"
                        + "<li>Choose any resource from the left-side menu</li> "
                        + "<li>The Help button has neat features</li> "
                        + "<li>Check for new Flash Cards and Games</li> "
                        + "<li>Use our whiteboard to work the problems</li>"
                        + "<li><a style='color: white' href='#' onclick='showMotivationalVideo_Gwt();return false;'>Professor Musa Video</li>"
                        + "</ul></div>";
                
                Html ohtml = new Html(html);
                ohtml.addStyleName("prescription-help-panel");
                CmMainPanel.__lastInstance._mainContent.removeAll();
                CmMainPanel.__lastInstance._mainContent.setLayout(new CenterLayout());
                CmMainPanel.__lastInstance._mainContent.add(ohtml);
                CmMainPanel.__lastInstance._mainContent.layout();

                ohtml.el().fadeIn(FxConfig.NONE);
            }
        };
        t.schedule(1);
    }

    LayoutContainer _main;

    public Widget getWestWidget() {
        _main = new LayoutContainer();
        _main.setLayout(new FitLayout());

        _guiWidget = new PrescriptionResourcePanel();
        _main.add(_guiWidget, new BorderLayoutData(LayoutRegion.CENTER, .75f));
        // get the data for the prescription from the database

        getAsyncDataFromServer(UserInfo.getInstance().getSessionNumber());

        _main.add(new PrescriptionInfoPanel(PrescriptionCmGuiDefinition.__instance), new BorderLayoutData(
                LayoutRegion.SOUTH, .30f));

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
        if (location != null) {
            if (location.getLocationType() == LocationType.PRESCRIPTION)
                sessionNumber = location.getLocationNumber();
        }

        // If we are currently on requested session, no need for server call
        if (isReady && UserInfo.getInstance().getSessionNumber() == sessionNumber) {
            // update the request resource viewed
            setLocation(location);
            return;
        }

        final int sessionNumberF = sessionNumber;

        // clear any existing resource
        CmMainPanel.__lastInstance._mainContent.removeResource();
        CmMainPanel.__lastInstance._mainContent.layout();

        CmBusyManager.setBusy(true);

        Log.info("PrescriptionCmGuiDefinition.getAsyncDataFromServer:" + sessionNumber + ", " + location);

        new RetryAction<RpcData>() {

            @Override
            public void attempt() {
                boolean updateActive = UserInfo.getInstance().isActiveUser();
                GetPrescriptionAction action = new GetPrescriptionAction(UserInfo.getInstance().getRunId(), sessionNumberF, updateActive);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }            
            @Override
            public void oncapture(RpcData rdata) {
                try {
                    if (rdata != null) {
                        UserInfo.getInstance().setSessionNumber(sessionNumberF);

                        int correctPercent = rdata.getDataAsInt("correct_percent");
                        UserInfo.getInstance().setCorrectPercent(correctPercent);
                        if (correctPercent == 100) {
                            getContext().doNext();
                            return;
                        }

                        String json = rdata.getDataAsString("json");
                        context.setPrescriptionData(new PrescriptionData(json));

                        UserInfo.getInstance().setSessionCount(context.prescriptionData.getSessionTopics().size());

                        isReady = true; // signal data is ready

                        _guiWidget.buildUi(context.prescriptionData);

                        ContextController.getInstance().setCurrentContext(context);

                        if (UserInfo.getInstance().isAutoTestMode()) {
                            context.runAutoTest();
                        }

                        setLocation(location);
                    } else {
                        CatchupMathTools.showAlert("There was a problem reading this prescription data");
                    }
                } catch (Exception e) {
                    Log.error("Error reading data from server", e);
                } finally {
                    CmBusyManager.setBusy(false);
                }

            }
        }.attempt();

    }

    /**
     * Maintain a shared list of registered resources to allow for easy access
     * to individual resource items.
     * 
     */
    static Map<String, List<InmhItemData>> _registeredResources = new HashMap<String, List<InmhItemData>>();

    /**
     * Update the current resource loaded
     * 
     * @param location
     */
    private void setLocation(CmLocation location) {
        if (location != null) {
            String resourceTypeToView = location.getResourceType();
            if (resourceTypeToView != null) {

                List<InmhItemData> resourceList = _registeredResources.get(resourceTypeToView);
                if (resourceList != null) {
                    // select the Nth item
                    if (location.getResourceNumber() > -1) {

                        InmhItemData itemData = resourceList.get(location.getResourceNumber());
                        CmMainPanel.__lastInstance._mainContent.showResource(itemData);

                    } else {
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

    /**
     * Mark the pid as being viewed called from external JS
     * 
     * called from CatchupMath.js
     * 
     * 
     * @TODO: remove access to globals
     * 
     * @param pid
     */
    static public void solutionHasBeenViewed_Gwt(String eventName) {
        __last_solution_item.setViewed(true);
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_SOLUTIONS_COMPLETE, __last_solution_item));
    }

    /**
     * This solution is being viewed. The first step of solution has been
     * requested
     * 
     * Show info message about show work on first solution view per session
     * 
     * @param pid
     */
    static public void solutionIsBeingViewed_Gwt(String pid) {
        if (!CatchupMath.__hasBeenInformedAboutShowWork) {
            CatchupMathTools
                    .showAlert("First try your best to answer this problem, showing the steps, by pressing the Show-Work link.");
            CatchupMath.__hasBeenInformedAboutShowWork = true;
        }
    }

    static private native void publishNative() /*-{
                                               $wnd.solutionHasBeenViewed_Gwt = @hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition::solutionHasBeenViewed_Gwt(Ljava/lang/String;);
                                               $wnd.solutionIsBeingViewed_Gwt = @hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition::solutionIsBeingViewed_Gwt(Ljava/lang/String;);
                                               }-*/;

    static {
        publishNative();

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_SOLUTION_SHOW) {
                    __last_solution_item = (InmhItemData) event.getEventData();
                } else if (event.getEventType() == EventType.EVENT_TYPE_SOLUTIONS_COMPLETE) {
                    // update the InmhItemData associated with the currently
                    // active solution

                    InmhItemData id = ((InmhItemData) event.getEventData());
                    __instance.markResourceAsViewed(id);
                } else if (event.getEventType() == EventType.EVENT_TYPE_RESOURCE_VIEWER_OPEN) {
                    // if not a required problem, then set as viewed
                    CmResourcePanel viewer = (CmResourcePanel) event.getEventData();

                    /**
                     * practice problems only get marked viewed, once they are
                     * fully complete (event EVENT_TYPE_SOLUTIONS_COMPLETE)
                     * 
                     * practice problems are tracked elsewhere. (where?)
                     */
                    if (viewer.getResourceItem() != null) {
                        String type = viewer.getResourceItem().getType();
                        if (!(type.equals("practice") || type.equals("cmextra")))
                            __instance.markResourceAsViewed(viewer.getResourceItem());
                    }
                } else if (event.getEventType() == EventType.EVENT_TYPE_RESOURCE_VIEWER_CLOSE) {
                    __instance.showHelpPanel();
                } else if (event.getEventType() == EventType.EVENT_TYPE_SOLUTION_FIF_CORRECT) {
                    /**
                     * an Solution FIF was entered correct, so we want to mark
                     * this (current) solution as having been complicated
                     */
                    solutionHasBeenViewed_Gwt(null);
                }
            }
        });
    }
}

/**
 * Defines the main accordion widget containing the resources
 * 
 * @author casey
 * 
 */
class PrescriptionResourcePanel extends LayoutContainer {

    static PrescriptionResourcePanel __instance;

    PrescriptionData pdata;
    List<PrescriptionSessionDataResource> registeredResources = new ArrayList<PrescriptionSessionDataResource>();

    public List<PrescriptionSessionDataResource> getRegisteredResources() {
        return registeredResources;
    }

    public void setRegisteredResources(List<PrescriptionSessionDataResource> registeredResources) {
        this.registeredResources = registeredResources;
    }

    boolean isReady;

    public PrescriptionResourcePanel() {
        __instance = this;
        setStyleName("prescription-resource-panel");
    }

    /**
     * The menu button that corresponds to the practice problems
     * 
     */
    ResourceMenuButton _practiceProblemButton, _lessonResource;

    /**
     * Build or rebuild the GUI from list of resource objects
     * 
     * @param resources
     */
    public void buildUi(PrescriptionData pdata) {
        List<PrescriptionSessionDataResource> resources = pdata.getCurrSession().getInmhResources();

        removeAll();
        registeredResources.clear();

        setScrollMode(Scroll.NONE);
        VerticalPanel vp = new VerticalPanel();
        addStyleName("prescription-cm-gui-definition-resource-panel");

        // setTitle("Choose a resource type, then click one of its items.");
        for (PrescriptionSessionDataResource resource : resources) {
            ResourceMenuButton btn = new ResourceMenuButton(resource);

            registeredResources.add(resource);
            if (resource.getType().equals("practice"))
                _practiceProblemButton = btn;
            else if (resource.getType().equals("review"))
                _lessonResource = btn;

            vp.add(btn);
        }

        /**
         * Add the standard resources
         * 
         */
        for (PrescriptionSessionDataResource type : new CmInmhStandardResources()) {
            registeredResources.add(type);

            Button btn = new ResourceMenuButton(type);
            vp.add(btn);
        }

        add(vp);
        
        
        int currentSession = UserInfo.getInstance().getSessionNumber();
        int  totalSessions = UserInfo.getInstance().getSessionCount();
        String  statusMsg = "Lesson " + (currentSession+1) + " of " + totalSessions;
        add(new StatusImagePanel(totalSessions, currentSession+1,"Lesson Status", statusMsg));
        layout();
    }

    /** Display item data as prescription resource */
    private void showResource(InmhItemData itemData) {
        CmMainPanel.__lastInstance._mainContent.showResource(itemData);
    }

    public void expandResourcePracticeProblems() {
        _practiceProblemButton.updateCheckMarks();
        _practiceProblemButton.showMenu();
    }

    /**
     * Expand the resource node exposing resource items
     * 
     * Just matches with 'startsWith'
     * 
     * Return the ResourceList expanded, or null if no match
     * 
     * 
     * @param resourceType
     *            The table of the resource type
     * 
     */
    public void expandResourceType(String resourceType) {
        // show the menu for named resource
        // CatchupMathTools.showAlert("Show resource type: " + resourceType);
    }

    static {
        /**
         * Setup a listen for solution view completions to all the updating of
         * GUI accordingly.
         */
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_SOLUTIONS_COMPLETE) {
                    __instance._practiceProblemButton.checkCompletion();
                }
            }
        });
    }
}
