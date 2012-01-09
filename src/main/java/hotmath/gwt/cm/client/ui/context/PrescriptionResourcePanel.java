package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SetLessonCompletedAction;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.util.StatusImagePanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;

/**
 * Defines the main accordion widget containing the resources
 * 
 * @author casey
 * 
 */
public class PrescriptionResourcePanel extends LayoutContainer {

    static PrescriptionResourcePanel __instance;
    PrescriptionData pdata;
    List<PrescriptionSessionDataResource> registeredResources = new ArrayList<PrescriptionSessionDataResource>();
    Map<String, ResourceMenuButton> resourceButtons = new HashMap<String, ResourceMenuButton>();

    boolean isReady;

    public PrescriptionResourcePanel() {
        __instance = this;
        setStyleName("prescription-resource-panel");
    }
    

    public List<PrescriptionSessionDataResource> getRegisteredResources() {
        return registeredResources;
    }

    public void setRegisteredResources(List<PrescriptionSessionDataResource> registeredResources) {
        this.registeredResources = registeredResources;
    }

    
    public void buildUi(PrescriptionData pdata) {
    	this.pdata = pdata;
    	buildUi_Old(pdata);
    }

    /** 
     * @param pdata
     */
    public void buildUi_Old(PrescriptionData pdata) {
        List<PrescriptionSessionDataResource> resources = pdata.getCurrSession().getInmhResources();

        removeAll();
        registeredResources.clear();
        
        setScrollMode(Scroll.NONE);
        VerticalPanel vp = new VerticalPanel();
        addStyleName("prescription-cm-gui-definition-resource-panel");

        boolean isCustomProgram = UserInfo.getInstance().isCustomProgram();

        PrescriptionSessionDataResource review=null;
        // setTitle("Choose a resource type, then click one of its items.");
        for (PrescriptionSessionDataResource resource : resources) {
            ResourceMenuButton btn = new ResourceMenuButton(resource);

            registeredResources.add(resource);

            resourceButtons.put(resource.getType(), btn);
            
            /** if a Custom Program, then make sure the results
             * button is disabled .. there are no quizzes.
             */
            if(resource.getType().equals("results")) {
            	review = resource;
            }
            else {
            	vp.add(btn);	
            }
            
        }
        add(vp);
        /**
         * Add the standard resources
         * 
         */
        VerticalPanel fs = new VerticalPanel();
        // <div style='margin-left: -7px;margin-top: 10px;font-size: .8em;color: white;'>Other Resources</div>
        
        fs.add(new Html("<hr class='resource-separator'/>"));
        ResourceMenuButton rbtn = new ResourceMenuButton(review);
        if(isCustomProgram) {
            rbtn.setEnabled(false);
        }
        fs.add(rbtn);
        
        for (PrescriptionSessionDataResource resource : new CmInmhStandardResources()) {
    		registeredResources.add(resource);
    		ResourceMenuButton btn = new ResourceMenuButton(resource);
    		fs.add(btn);
    		resourceButtons.put(resource.getType(), btn);
        }
        add(fs);
        
        int numCompleteSessions = pdata.getCountCompletedTopics();
        int  totalSessions = UserInfo.getInstance().getSessionCount();
        String  statusMsg = "Completed " + numCompleteSessions  + " lessons of " + totalSessions;
        add(new StatusImagePanel(totalSessions, numCompleteSessions,"Lesson Status", statusMsg));
        
        layout();
    }


    /**
     * Build or rebuild the GUI from list of resource objects
     * 
     * @param resources
     */
//    public void buildUi_new(PrescriptionData pdata) {
//        List<PrescriptionSessionDataResource> resources = pdata.getCurrSession().getInmhResources();
//
//        removeAll();
//        registeredResources.clear();
//
//        
//        add(new LocationBar());
//        
//        
//        setScrollMode(Scroll.NONE);
//        VerticalPanel vp = new VerticalPanel();
//        addStyleName("prescription-cm-gui-definition-resource-panel");
//
//        boolean isCustomProgram = UserInfo.getInstance().isCustomProgram();
//
//        PrescriptionSessionDataResource review=null;
//        // setTitle("Choose a resource type, then click one of its items.");
//        for (PrescriptionSessionDataResource resource : resources) {
//            ResourceMenuButton btn = new ResourceMenuButton(resource);
//
//            registeredResources.add(resource);
//
//            resourceButtons.put(resource.getType(), btn);
//            
//            /** if a Custom Program, then make sure the results
//             * button is disabled .. there are no quizzes.
//             */
//            if(resource.getType().equals("results")) {
//            	review = resource;
//            }
//            else {
//            	vp.add(btn);	
//            }
//            
//        }
//        add(vp);
//        /**
//         * Add the standard resources
//         * 
//         */
//        VerticalPanel fs = new VerticalPanel();
//        // <div style='margin-left: -7px;margin-top: 10px;font-size: .8em;color: white;'>Other Resources</div>
//        
//        fs.add(new Html("<hr class='resource-separator'/>"));
//        ResourceMenuButton rbtn = new ResourceMenuButton(review);
//        if(isCustomProgram) {
//            rbtn.setEnabled(false);
//        }
//        fs.add(rbtn);
//        
//        for (PrescriptionSessionDataResource type : new CmInmhStandardResources()) {
//    		registeredResources.add(type);
//    		ResourceMenuButton btn = new ResourceMenuButton(type);
//    		fs.add(btn);
//    		resourceButtons.put(type.getType(), btn);
//        }
//        add(fs);
//        
//        int currentSession = UserInfo.getInstance().getSessionNumber();
//        int  totalSessions = UserInfo.getInstance().getSessionCount();
//        String  statusMsg = "Lesson " + (currentSession+1) + " of " + totalSessions;
//        add(new StatusImagePanel(totalSessions, currentSession+1,"Lesson Status", statusMsg));
//        
//        layout();
//    }
    
    /** make an indication that the current RPP has completed
     * 
     */
    public void indicateRequiredPracticeComplete() {
    	resourceButtons.get("practice").el().blink(new FxConfig());
    }

    public void expandResourcePracticeProblems() {
        resourceButtons.get("practice").updateCheckMarks();
        resourceButtons.get("practice").showMenu();
    }
    
    public void disableGames() {
        
        resourceButtons.get("activity_standard").disable();
        resourceButtons.get("flashcard").disable();
        resourceButtons.get("flashcard_spanish").disable();
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
            public void handleEvent(CmEvent event) {
            	
            	switch(event.getEventType()) {
            	case EVENT_TYPE_REQUIRED_COMPLETE:
                    InmhItemData id = (InmhItemData)event.getEventData();
                    boolean isComplete = __instance.resourceButtons.get("practice").checkCompletion();
                    
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
