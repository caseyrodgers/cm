package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SetLessonCompletedAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.ResourceMenuButton;
import hotmath.gwt.cm_tools.client.ui.ResourceMenuButton.RegisterCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.ui.resource.CmInmhStandardResources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.fx.client.FxElement;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/**
 * Defines the main accordion widget containing the resources
 * 
 * @author casey
 * 
 */
public class PrescriptionResourcePanel extends FlowLayoutContainer {

    static PrescriptionResourcePanel __instance;
    PrescriptionData pData;
    Map<String, ResourceMenuButton> resourceButtons = new HashMap<String, ResourceMenuButton>();

    boolean isReady;

    public PrescriptionResourcePanel() {
        __instance = this;
        setStyleName("prescription-resource-panel");
        
        setScrollMode(ScrollMode.AUTOY);
    }
    
    public void buildUi(PrescriptionData pData) {
    	this.pData = pData;
        List<PrescriptionSessionDataResource> resources = pData.getCurrSession().getInmhResources();

        clear();
        
        //setScrollMode(ScrollMode.NONE);
        addStyleName("prescription-cm-gui-definition-resource-panel");

        boolean isCustomProgram = UserInfo.getInstance().isCustomProgram();

        
        PrescriptionSessionDataResource review=null;
        // setTitle("Choose a resource type, then click one of its items.");
        
        ResourceMenuButton.RegisterCallback callback = new RegisterCallback() {
            @Override
            public void registerItems(String label, List<InmhItemData> resouresToRegister) {
                PrescriptionCmGuiDefinition._registeredResources.put(label, resouresToRegister);                
            }
            
            @Override
            public void loadResourceIntoHistory(String label, String resource) {
                CmHistoryManager.loadResourceIntoHistory(label, resource);
            }
        };
        
        for (PrescriptionSessionDataResource resource : resources) {
            
            /** do not show EPPs
             * 
             */
            if(resource.getType() == CmResourceType.CMEXTRA) {
                continue;
            }
            
            
            if(resource.getType() == CmResourceType.WEBLINK) {
                if(resource.getItems() != null && resource.getItems().size() == 1) {
                    resource.setLabel(resource.getItems().get(0).getTitle());
                }
            }
            
            ResourceMenuButton btnResource = new ResourceMenuButton(resource, callback);

            resourceButtons.put(resource.getType().label(), btnResource);
            
            /** if a Custom Program, then make sure the results
             * button is disabled .. there are no quizzes.
             */
            if(resource.getType() == CmResourceType.RESULTS) {
            	review = resource;
            }
            else {
                add(btnResource);
                add(createFiller());
            }
            
        }

        //flowLayoutMain.add(vertPanelResources);

        /**
         * Add the standard resources
         * 
         */

        add(new HTML("<hr class='resource-separator'/>"));
        add(createFiller());

        ResourceMenuButton rbtn = new ResourceMenuButton(review, callback);
        if(isCustomProgram) {
            rbtn.setEnabled(false);
        }
        add(rbtn);
        add(createFiller());

        for (PrescriptionSessionDataResource resource : new CmInmhStandardResources()) {
    		ResourceMenuButton btn = new ResourceMenuButton(resource, callback);
    		add(btn);
    		add(createFiller());
    		resourceButtons.put(resource.getType().label(), btn);
        }
    }

    
    
    
    private Widget createFiller() {
    	return new HTML("<div class='resource-filler'> &nbsp; </div>");
    }

	/** make an indication that the current RPP has completed
     * 
     */
    public void indicateRequiredPracticeComplete() {
    	resourceButtons.get(CmResourceType.PRACTICE.label()).getElement().<FxElement>cast().blink();
    }

    public void updateCheckMarks(CmResourceType resourceType) {
        
        if(resourceType != null && resourceType == CmResourceType.CMEXTRA) {
            resourceButtons.get(resourceType.label()).updateCheckMarks();
            //resourceButtons.get("cmextra").showMenu();
        }
        else {
            resourceButtons.get(CmResourceType.PRACTICE.label()).updateCheckMarks();
            if(resourceButtons.get(CmResourceType.PRACTICE.label()).getMenu() != null) {
                resourceButtons.get(CmResourceType.PRACTICE.label()).showMenu();
            }
        }
    }
    
    public void disableGames() {
        
        resourceButtons.get(CmResourceType.ACTIVITY_STANDARD.label()).disable();
        resourceButtons.get(CmResourceType.FLASHCARD.label()).disable();
        resourceButtons.get(CmResourceType.FLASHCARD_SPANISH.label()).disable();
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
         * Setup a listener for solution view completions.
         * 
         *  Also update DB via RPC when Lesson is complete
         */
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            @SuppressWarnings("incomplete-switch")
			public void handleEvent(CmEvent event) {
            	
            	switch(event.getEventType()) {
            	case EVENT_TYPE_REQUIRED_COMPLETE:
                    InmhItemData id = (InmhItemData)event.getEventData();
                    id.setViewed(true);
                    boolean isComplete = __instance.resourceButtons.get(CmResourceType.PRACTICE.label()).checkCompletion();
                    if (isComplete) {
                    	setLessonCompleted(id.getTitle());
                    }
            	}
            }
        });

    }
    
    public static void setLessonCompleted(final String title) {
    	
    	final int runId = UserInfo.getInstance().getRunId();
    	final int session = UserInfo.getInstance().getSessionNumber();
    	
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
            	SetLessonCompletedAction action = new SetLessonCompletedAction(title, runId, session);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            @Override
            public void oncapture(RpcData userAdvance) {
                CmLogger.info("SetLessonCompletedAction complete: " + userAdvance);
            }
        }.register();
    	
    	
    }

}
