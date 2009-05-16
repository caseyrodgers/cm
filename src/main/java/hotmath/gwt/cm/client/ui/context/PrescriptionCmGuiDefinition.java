package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.data.PrescriptionData;
import hotmath.gwt.cm.client.data.PrescriptionSessionDataResource;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.CmGuiDefinition;
import hotmath.gwt.cm.client.ui.CmMainPanel;
import hotmath.gwt.cm.client.ui.ContextController;
import hotmath.gwt.cm.client.ui.HeaderPanel;
import hotmath.gwt.cm.client.ui.viewer.ResourceViewer;
import hotmath.gwt.cm.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm.client.util.UserInfo;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.IconButton;
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
        CatchupMath.setBusy(true);

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
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");

        CatchupMath.setBusy(true);

        boolean updateActive = UserInfo.getInstance().isActiveUser();
        s.getPrescriptionSessionJson(UserInfo.getInstance().getRunId(), sessionNumber, updateActive,
                new AsyncCallback() {
                    public void onSuccess(Object result) {
                        RpcData rdata = (RpcData) result;
                        if(rdata == null) {
                            CatchupMath.showAlert("There was a problem reading this prescription data");
                            return;
                        }
                        try {
                            String programTitle = rdata.getDataAsString("program_title");
                            UserInfo.getInstance().setTestName(programTitle);
                            
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

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            CatchupMath.setBusy(false);
                        }
                    }
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }                    
                });
    }

    public Widget getCenterWidget() {
        return null;
    }

    public String getTitle() {
        return "Prescription Resource";
    }
    
    static public void solutionHasBeenViewed_Gwt(String pid) {
        // mark the currently viewed resource as being viewed
        ResourceModel rm = PrescriptionResourceAccord.__instance._activeResourceList.getSelectionModel().getSelectedItem();
        InmhItemData itemData = rm.getItem();
        PrescriptionResourceAccord.__instance._activeResourceList.markResourceAsViewed(itemData);
    }
    
    static private native void publishNative() /*-{
        $wnd.solutionHasBeenViewed_Gwt = @hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition::solutionHasBeenViewed_Gwt(Ljava/lang/String;);
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
        Html html = new Html("Loading your personal set of review and practice problems...");
        html.setStyleName("resource-accord-panel-loading");
        add(html);
    }

    /**
     * Build or rebuild the GUI from list of resource objects
     * 
     * @param resources
     */
    ResourceList _activeResourceList;
    public void buildUi(PrescriptionData pdata) {

        List<PrescriptionSessionDataResource> resources = pdata.getCurrSession().getInmhResources();

        removeAll();

        AccordionLayout al = new AccordionLayout();
        setLayout(al);
        al.setActiveOnTop(false);
        
        // for each distinct resource type
        ContentPanel cp = null;
        ResourceList rl = null;

        setTitle("Choose a resource type, then click one of its items.");
        for (PrescriptionSessionDataResource resource : resources) {
            cp = new ContentPanel();
            cp.addStyleName("accordian-resource-list-panel");
            cp.setLayout(new FitLayout());
            cp.setHeading(resource.getLabel());
            cp.getHeader()
                    .addTool(new Html("<img class='resource-type' src='/gwt-resources/images/check_black.png'/>"));
            // to show check
            // cp.addStyleName("accordian-resource-list-panel-complete");
            cp.setBodyStyleName("pad-text");
            // cp.setScrollMode(Scroll.AUTO);

            rl = new ResourceList(resource);
            if (rl.getStore().getCount() == 0)
                cp.setEnabled(false);
            if (rl.allViewed() && cp.isEnabled()) {
                // this listView needs to be marked complete
                cp.getHeader().addStyleName("resource-type-complete");
                PrescriptionResourceAccord.__instance.layout();
            }

            cp.setAnimCollapse(false);
            cp.collapse();
            cp.add(rl);

            add(cp);
            final ContentPanel mycp = cp;

            final ContentPanel thisCp = cp;
            final ResourceList thisRl = rl;
            cp.addListener(Events.Expand, new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    CmMainPanel.__lastInstance._mainContent.removeAll();
                    layout();
                    
                    // move selected item to top
                    PrescriptionResourceAccord.this.remove(mycp);
                    //mycp.el().fadeIn(FxConfig.NONE);
                    PrescriptionResourceAccord.this.add(mycp);
                    layout();
                    
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
        
//        // should we select all
//        if(rl._store.getCount() > 0) {
//            rl.getSelectionModel().select(0, false);
//            rl.loadResource(rl.getSelectionModel().getSelectedItem());
//            layout();
//        }
        
        // try to expand the last resource type
        try {
            cp.expand();
            layout();
            al.setActiveItem(cp);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        layout();
    }
    
    public void expandResourcePracticeProblems() {
        expandResourceType("Required Problems");
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
            if (_viewer != null)
                _viewer.removeResourcePanel();

            // is there a resource file?
            if (resourceItem.getFile() == null || resourceItem.getFile().length() == 0)
                return;

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
            cp.el().fadeIn(FxConfig.NONE);

            
            // only mark NON-SOLUTION resources as viewed when simply
            // viewing them.  The solutions are marked as viewed when 
            // the last step has been viewed.
            if(!resourceItem.getType().equals("practice"))
                markResourceAsViewed(resourceItem);
            
        } catch (Exception hme) {
            hme.printStackTrace();
            CatchupMath.showAlert("Error: " + hme.getMessage());
        }
    }
    
    
    public void markResourceAsViewed(final InmhItemData resourceItem) {
        /**
         * 
         * mark this INMH resource item as being viewed
         * 
         */
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.setInmhItemAsViewed(UserInfo.getInstance().getRunId(), resourceItem.getType(), resourceItem.getFile(),
                new AsyncCallback() {

                    public void onSuccess(Object result) {
                        resourceItem.setViewed(true);

                        // update the total count in the Header
                        int vc = UserInfo.getInstance().getViewCount();
                        UserInfo.getInstance().setViewCount(++vc);
                        HeaderPanel.__instance.setLoginInfo();

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

        if (item.isViewed()) {
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
