package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
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
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.shared.client.rpc.action.GetPrescriptionAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class PrescriptionCmGuiDefinition implements CmGuiDefinition {

    PrescriptionContext context;
    PrescriptionResourceAccord _guiWidget;
    boolean isReady;
    static public PrescriptionCmGuiDefinition __instance;

    /**
     * Create and read Prescription data for the current user.
     * 
     */
    public PrescriptionCmGuiDefinition() {
        __instance = this;
        context = new PrescriptionContext(this);
    }

    public CmContext getContext() {
        return context;
    }


    LayoutContainer _main;
    public Widget getWestWidget() {
        CatchupMathTools.setBusy(true);

        _main = new LayoutContainer();
        _main.setLayout(new BorderLayout());
        
        _guiWidget = new PrescriptionResourceAccord();
        _main.add(_guiWidget, new BorderLayoutData(LayoutRegion.CENTER));
        // get the data for the prescription from the database

        getAsyncDataFromServer(UserInfo.getInstance().getSessionNumber());

        
        PrescriptionInfoPanel infoPanel = new PrescriptionInfoPanel(this);
        _main.add(infoPanel, new BorderLayoutData(LayoutRegion.SOUTH, .30f));

        return _main;
    }

    /**
     * Read data from server and build UI when complete
     * 
     */
    public void getAsyncDataFromServer(int sessionNumber) {

        // clear any existing resource
        CmMainPanel.__lastInstance._mainContent.removeAll();
        CmMainPanel.__lastInstance._mainContent.layout();

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

    public Widget getCenterWidget() {
        return null;
    }

    public String getTitle() {
        return "Prescription Resource";
    }
    
    /** Mark the pid as being viewed
     * 
     * @TODO: remove access to globals
     * 
     * @param pid
     */
    static public void solutionHasBeenViewed_Gwt(String eventName) {
        InmhItemData itemData = PrescriptionResourceAccord.__instance.getCurrentSolutionResource();
        System.out.println("Solution has been viewed: " + itemData.getFile());
        PrescriptionResourceAccord.__instance._activeResourceList.markResourceAsViewed(itemData);
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
    }
    
}

class PrescriptionResourceAccord extends LayoutContainer {

    static PrescriptionResourceAccord __instance;

    PrescriptionData pdata;
    boolean isReady;
    public PrescriptionResourceAccord() {
        __instance = this;
        
        setStyleName("resource-accord-panel");
        Html html = new Html("");
        html.setStyleName("resource-accord-panel-loading");
        add(html);
        
    }

    
    /** The current active resource list
     * 
     */
    ResourceList _activeResourceList;
    
    
    /**
     * Build or rebuild the GUI from list of resource objects
     * 
     * @param resources
     */
    public void buildUi(PrescriptionData pdata) {

        _activeResourceList = null;
        
        List<PrescriptionSessionDataResource> resources = pdata.getCurrSession().getInmhResources();

        removeAll();
        
        AccordionLayout al = new AccordionLayout();
        setLayout(al);
        al.setActiveOnTop(false);
        al.setFill(true);
        
        // for each distinct resource type
        ContentPanel cp = null;
        ResourceList rl = null;

        // setTitle("Choose a resource type, then click one of its items.");
        for (PrescriptionSessionDataResource resource : resources) {
            cp = new ContentPanel();
            cp.setAnimCollapse(false);
            cp.collapse();
            cp.addStyleName("accordian-resource-list-panel");
            cp.setLayout(new FitLayout());
            // to show check
            cp.setBodyStyleName("pad-text");
            // cp.setScrollMode(Scroll.AUTO);
            
            
            fixupResourceItems(resource);

            cp.setHeading(resource.getLabel());
            cp.setToolTip(resource.getDescription());
            cp.getHeader().addTool(new Html("<img class='resource-type' src='/gwt-resources/images/check_black.png'/>"));

            rl = new ResourceList(resource);
            if(_activeResourceList == null) {
                _activeResourceList = rl;
            }
            if (rl.getStore().getCount() == 0)
                cp.setEnabled(false);
            if (rl.allViewed() && cp.isEnabled()) {
                // this listView needs to be marked complete
                if(resource.getType().equals("practice")) {
                    // only mark the practice resource
                    cp.getHeader().addStyleName("resource-type-complete");
                    PrescriptionResourceAccord.__instance.layout();
                }
            }
            // cp.setAnimCollapse(true);
            cp.setHeight(150);
            cp.add(rl);

            add(cp);
            final ContentPanel mycp = cp;

            final ContentPanel thisCp = cp;
            final ResourceList thisRl = rl;
            cp.addListener(Events.Expand, new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    if(PrescriptionResourceAccord.__instance._activeResourceList != null)
                        if(PrescriptionResourceAccord.__instance._activeResourceList._viewer != null)
                            PrescriptionResourceAccord.__instance._activeResourceList._viewer.removeResourcePanel();
                    
                    PrescriptionResourceAccord.__instance._activeResourceList.removeResource();
                    layout();
                    
                    //// move selected item to top
                    //PrescriptionResourceAccord.this.remove(mycp);
                    //mycp.el().fadeIn(FxConfig.NONE);
                    //PrescriptionResourceAccord.this.add(mycp);
                    //layout();
                    
                    // if there is only one resource item, then show it
                    if(mycp.getItems().size() == 1) {
                        ResourceList myRl = (ResourceList)mycp.getItem(0);
                        if(myRl.getItemCount() ==1) {
                            myRl.getSelectionModel().select(0, false);
                            myRl.loadResource(myRl.getSelectionModel().getSelectedItem());
                        }
                    }
                    
                    _activeResourceList = thisRl;
                }
            });
            cp.addListener(Events.Collapse, new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    // ResourceAccordPanel._mainContent.removeAll();
                }
            });
        }
        
        /** Should we expand the last resource type added?
         * 
        try {
            cp.expand();
            layout();
            al.setActiveItem(cp);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        */

        layout();
    }
    
    
    /** Return the INMH data for the currently selected item
     * 
     * @return
     */
    public InmhItemData getCurrentSolutionResource() {
       return _activeResourceList.getSelectionModel().getSelectedItem().getItem();
    }
    
    /** Provide any last minute modification to the list items
     *  Such as assigning unique titles, as with the the cmextra
     *  
     *  @TODO: perhaps, assign a title when setting up .inmh_link file
     * @param resource
     */
    private void fixupResourceItems(PrescriptionSessionDataResource resource) {
        
        if(resource.getType().equals("cmextra")) {
            // create sequenced titles
            int cnt=0;
            for(InmhItemData id: resource.getItems()) {
                id.setTitle("Extra Problem " + (++cnt));
            }
        }
    }
    
    public void expandResourcePracticeProblems() {
        expandResourceType("Required Practice Problems");
    }
    
    /** Expand the resource node exposing resource items
     * 
     * @param resourceType The table of the resource type
     * 
     */
    public void expandResourceType(String resourceType) {
        for(int i=0, t=getItems().size();i<t;i++) {
            if(getItem(i) instanceof ContentPanel) {
                ContentPanel cp = (ContentPanel)getItem(i);
                String title = cp.getHeader().getText();
                if(title.equals(resourceType)) {
                    cp.expand();
                    break;
                }
            }
        }
    }
}

class ResourceList extends ListView<ResourceModel> implements Listener {

    ListStore<ResourceModel> _store;
    PrescriptionSessionDataResource resource;

    public ResourceList(PrescriptionSessionDataResource resource) {
        this.resource = resource;
        setStyleName("resource-accord-panel-list-view");
        _store = new ListStore<ResourceModel>();
        for (InmhItemData id : resource.getItems()) {
            _store.add(new ResourceModel(id));
        }
        setStore(_store);
        // allViewed();
        setSimpleTemplate("<div class='resource-item'>{title}&nbsp;<img id='{file}' class='{completeClassName}' src='/gwt-resources/images/check_white.png'/></div>");
        setPosition(10, 10);

        addListener(Events.Select, this);
    }

    public void handleEvent(BaseEvent be) {
        ResourceModel rm = getSelectionModel().getSelectedItem();
        loadResource(rm);
    }

    public void loadResource(ResourceModel rm) {
        showResource(rm.getItem());
    }

    public boolean allViewed() {
        int viewed = 0;
        for (InmhItemData id : resource.getItems()) {
            if (id.isViewed())
                viewed++;
        }
        return getStore().getCount() == viewed;
    }

    /**
     * Display resource in the proper viewer
     * 
     * @param resourceItem
     */
    ResourceViewer _viewer;

    public void showResource(final InmhItemData resourceItem) {
        try {
            
            Log.debug("PrescriptionCmGuiDefinition.showResource: showing resource: " + resourceItem.getFile());
            
            if (_viewer != null)
                _viewer.removeResourcePanel();


            _viewer = ResourceViewerFactory.create(resourceItem.getType());

            CmMainPanel.__lastInstance._mainContent.removeAll();

            //CmMainPanel.__lastInstance._mainContent.setLayout(new FitLayout());

            // must be a ContentPanel
            LayoutContainer cp = (LayoutContainer) _viewer.getResourcePanel(resourceItem);



            //DecoratorPanel dp = new DecoratorPanel();
            //dp.setStyleName("resource-wrapper-decorator");
            //dp.add(cp);
            //hp.add(dp);
            CmMainPanel.__lastInstance._mainContent.add(cp);
            CmMainPanel.__lastInstance._mainContent.layout();
            CmMainPanel.__lastInstance._mainContent.resetChildSize();
            //cp.el().fadeIn(FxConfig.NONE);

            // practice problems/extra problems are marked only when last step is viewed
            if(resourceItem.getType().equals("practice") || resourceItem.getType().equals("cmextra")) {
                return;
            }
            
            markResourceAsViewed(resourceItem);
            
        } catch (Exception hme) {
            hme.printStackTrace();
            CatchupMathTools.showAlert("Error: " + hme.getMessage());
        }
    }
    
    public void removeResource() {
        CmMainPanel.__lastInstance._mainContent.removeAll();
    }
    
    
    /** Mark this resource as being viewed, should show
     *  some visual clues.
     *   
     * @param resourceItem
     */
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

                        int which = -1;
                        ResourceModel rm = getSelectionModel().getSelectedItem();
                        for (int i = 0; i < getStore().getCount(); i++) {
                            if (rm == getStore().getAt(i)) {
                                getElement(i).getElementsByTagName("img").getItem(0).setClassName("resource-item-complete");
                                PrescriptionResourceAccord.__instance.layout();
                            }
                        }

                        if (allViewed()) {
                            getParent().addStyleName("resource-type-complete");
                            PrescriptionResourceAccord.__instance.layout();
                        }

                    }

                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                });
        
    }
}

class ResourceModel extends BaseModelData {
    InmhItemData item;

    public ResourceModel(InmhItemData item) {
        this.item = item;
        set("title", item.getTitle());
        set("type", item.getType());
        set("file", item.getFile());

        if (item.getType().equals("practice") && item.isViewed()) {
            set("completeClassName", "resource-item-complete");
        } else {
            set("completeClassName", "resource-item");
        }
    }

    public InmhItemData getItem() {
        return item;
    }

    public void setItem(InmhItemData item) {
        this.item = item;
    }
}
