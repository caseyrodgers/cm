package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.history.CmHistoryQueue;
import hotmath.gwt.cm.client.history.CmLocation;
import hotmath.gwt.cm.client.history.CmLocation.LocationType;
import hotmath.gwt.cm.client.ui.EndOfProgramPanel;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmGuiDefinition;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class PrescriptionCmGuiDefinition implements CmGuiDefinition {

    PrescriptionContext context;
    boolean isReady;
    static public PrescriptionCmGuiDefinition __instance;
    static public InmhItemData __last_solution_item;
    public PrescriptionResourcePanel _guiWidget;

    /**
     * Create and read Prescription data for the current user.
     * 
     */
    public PrescriptionCmGuiDefinition() {
        __instance = this;
        context = new PrescriptionContext(this);
    }

   
    FlowLayoutContainer _main;

    private Widget createGui() {
        _main = new FlowLayoutContainer();

        _guiWidget = new PrescriptionResourcePanel();
        _main.add(_guiWidget);

        //BorderLayoutData southData = new BorderLayoutData(.30f);
        _main.add(new PrescriptionInfoPanel(PrescriptionCmGuiDefinition.__instance));
        
        
        new Timer() {
            @Override
            public void run() {
                showHelpPanel();
            }
        }.schedule(1000);
        
        return _main;
    }
    
    public Widget getWestWidget() {
        return createGui();
    }

    /**
     * Read data for a single lesson from server 
     * and populate UI when complete
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
        CmMainPanel.getActiveInstance().removeResource();

        CmBusyManager.setBusy(true);

        CmLogger.info("PrescriptionCmGuiDefinition.getAsyncDataFromServer:" + sessionNumber + ", " + location);

        new RetryAction<PrescriptionSessionResponse>() {

            @Override
            public void attempt() {
                boolean updateActive = UserInfo.getInstance().isActiveUser();
                GetPrescriptionAction action = new GetPrescriptionAction(UserInfo.getInstance().getRunId(), sessionNumberF, updateActive);
                setAction(action);
                CmRpcCore.getCmService().execute(action,this);
            }            
            @Override
            public void oncapture(PrescriptionSessionResponse prescriptionResponse) {
                try {
                    setPrescriptionData(prescriptionResponse,sessionNumberF);
                } catch (Exception e) {
                	CmLogger.error("Error reading data from server", e);
                	if(UserInfo.getInstance().isCustomProgram()) {
                	    new EndOfProgramPanel();
                	}
                } finally {
                    CmBusyManager.setBusy(false);
                }

            }
        }.register();

    }

    
    public void setPrescriptionData(PrescriptionSessionResponse prescriptionResponse, int sessionNumber) {
        if (prescriptionResponse != null) {
            CmLogger.debug("Data: " + prescriptionResponse + ", Context: " + context + ", UserInfo: " + UserInfo.getInstance());
            
            UserInfo.getInstance().setSessionNumber(sessionNumber);

            context.setPrescriptionData(prescriptionResponse.getPrescriptionData());
            
            UserInfo.getInstance().setSessionCount(context.prescriptionData.getSessionTopics().size());
            
            isReady = true; // signal data is ready

            _guiWidget.buildUi(context.prescriptionData);

            ContextController.getInstance().setCurrentContext(context);

            if (UserInfo.getInstance().isAutoTestMode()) {
                context.runAutoTest();
            }

            // setLocation(location);
        } else {
            CatchupMathTools.showAlert("There was a problem reading this prescription data");
        }
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
                        CmMainPanel.getActiveInstance().showResource(itemData);

                    } else {
                        String resourceId = location.getResourceId();
                        InmhItemData item = new InmhItemData();
                        item.setType(CmResourceType.mapResourceType(resourceTypeToView));
                        item.setFile(resourceId);
                        CmMainPanel.getActiveInstance().showResource(item);
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
    
    
    public void disableGameResources() {
        _guiWidget.disableGames();
    }

    /**
     * Mark resource as viewed
     * 
     * @param resourceItem
     */
    public void markResourceAsViewed(final InmhItemData resourceItem) {
        if (UserInfo.getInstance().getRunId() == 0) {
            CmLogger.info("PrescriptionCmGuiDefinition: run_id is null!");
        }


        markResourceAsViewed(resourceItem, new CallbackOnComplete() {
            @Override
            public void isComplete() {
                _guiWidget.updateCheckMarks(resourceItem.getType());
            }
        });
    }
    
    static public void markResourceAsViewed(final InmhItemData resourceItem, final CallbackOnComplete callback) {
        /**
         * 
         * mark this INMH resource item as being viewed
         * 
         */
        new RetryAction<RpcData>() {

            @Override
            public void attempt() {
                int sessionNumber = UserInfo.getInstance().getSessionNumber();
                SetInmhItemAsViewedAction action = new SetInmhItemAsViewedAction(UserInfo.getInstance().getRunId(), resourceItem.getType(),resourceItem.getFile(),sessionNumber);
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }
            
            public void oncapture(RpcData result) {

                CmLogger.debug("PrescriptionResourceAccord: setItemAsViewed: " + resourceItem);

                boolean isSolutionResource = (resourceItem.getType().equals(CmResourceType.PRACTICE) || resourceItem.getType().equals(CmResourceType.CMEXTRA));

                // update the total count in the Header
                // only if a practice or cmextra type
                int vc = UserInfo.getInstance().getViewCount();
                if (isSolutionResource && !resourceItem.isViewed()) {
                    vc++;
                    UserInfo.getInstance().setViewCount(vc);
                }
                HeaderPanel.__instance.setLoginInfo();

                // only mark practice problems
                // 
                if (!isSolutionResource)
                    return;

                resourceItem.setViewed(true);
                
                callback.isComplete();
            }

        }.register();
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
    static public void showHelpPanel() {
        Timer t = new Timer() {
            @Override
            public void run() {
            	Hyperlink hLink = CatchupMath.getStudentHowToVideoHyperlink();
        		hLink.getElement().setAttribute("style", "margin-left:15px; margin-top:15px; text-decoration:underline; color:white; cursor:pointer;");

                String html = 
                          "<div style='width: 400px;margin: auto;'>" 
                        + "<b>Catchup Math: the more you do, the more you learn!</b></div>";
                
                HTML ohtml = new HTML(html);
                //ohtml.addStyleName("prescription-help-panel");
                CmMainPanel.getActiveInstance().showCenterMessage(ohtml, hLink);
                //ohtml.getElement().<FxElement>cast().fadeToggle();
            }
        };
        t.schedule(1);
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
                                               $wnd.solutionIsBeingViewed_Gwt = @hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition::solutionIsBeingViewed_Gwt(Ljava/lang/String;);
                                               }-*/;

    static {
        publishNative();

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @Override
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_SOLUTION_SHOW) {
                    __last_solution_item = (InmhItemData) event.getEventData();
                } else if (event.getEventType() == EventType.EVENT_TYPE_REQUIRED_COMPLETE || event.getEventType() == EventType.EVENT_TYPE_EPP_COMPLETE) {
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
                        CmResourceType type = viewer.getResourceItem().getType();
                        if (!(type == CmResourceType.PRACTICE) || type == CmResourceType.CMEXTRA) {
                            __instance.markResourceAsViewed(viewer.getResourceItem());
                        }
                    }
                } else if (event.getEventType() == EventType.EVENT_TYPE_RESOURCE_VIEWER_CLOSE) {
                    showHelpPanel();
                } else if (event.getEventType() == EventType.EVENT_TYPE_SOLUTION_FIF_CORRECT) {
                    /**
                     * a Solution FIF was entered correct, so we want to mark
                     * this (current) solution as having been completed
                     */
                    ResourceViewerImplTutor2.solutionHasBeenViewed_Gwt(null);
                }
            }
        });
    }
}

