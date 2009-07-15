package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends ResourceViewerContainer implements ResourceViewer {
    static ResourceViewerImplTutor _instance;
    static {
        publishNative();
        
        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if(event.getEventName().equals(EventBus.EVENT_TYPE_WHITEBOARDUPDATED)) {
                    _instance.whiteBoardHasBeenUpdated((String)event.getEventData());
                }
            }
        });
    }
    
    CardLayout _cardLayout = new CardLayout(); 
    public ResourceViewerImplTutor() {
        _instance = this;
        addStyleName("resource-viewer-impl-tutor");
        
        setNoHeaderOrFooter();
        
        _tabSolution = new LayoutContainer();
        _tabSolution.setLayout(new FitLayout());
        _tabSolution.setScrollMode(Scroll.AUTOY);
        _tabShowWork = new LayoutContainer(new FitLayout());
        _tabShowWork.setScrollMode(Scroll.AUTOY);

        setLayout(_cardLayout);

        add(_tabSolution);
        add(_tabShowWork);
        
        setStyleAttribute("background", "transparent");
        _tabShowWork.setStyleAttribute("background","yellow");
        _cardLayout.setActiveItem(_tabSolution);
    }

    Button showWorkBtn, hideWorkBtn;
    String pid;
    boolean hasShowWork;
    InmhItemData resource;
    LayoutContainer _tabSolution, _tabShowWork;
    
    public Widget getResourcePanel(final InmhItemData resource) {
        this.pid = resource.getFile();
        this.resource = resource;
        showSolution();
        
        return this;
    }

    static Window showWorkWin;
    public void removeResourcePanel() {
        if (showWorkWin != null) {
            showWorkWin.hide();
            layout();
        }
    }
    
    @Override
    public boolean shouldSetResourceContinerHeight() {
        return true;
    }
    
    
    public double getAllowedVerticalSpace() {
        return .98;
    }
    
    
    /** Load the tutor 
     * 
     */
    public void showSolution() {
        
        Log.debug("ResourceViewerImplTutor: loading solution '" + pid + "'");
        
        // call for the solution HTML
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getSolutionHtml(UserInfo.getInstance().getUid(), pid, new AsyncCallback<RpcData>() {
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }

            public void onSuccess(RpcData result) {
                String html = result.getDataAsString("solutionHtml");
                boolean hasShowWork = result.getDataAsInt("hasShowWork") > 0;
                
                Html htmlO = new Html(html);
                htmlO.setStyleName("tutor_solution_wrapper");

                //addResource(ResourceViewerImplTutor.this,resource.getTitle());

                
                 _tabSolution.add(htmlO);
                //_tabSolution.add(new Label("TEST TEST"));
                _cardLayout.setActiveItem(_tabSolution);
                

                setNoHeaderOrFooter();
              
                //CmMainPanel.__lastInstance._mainContent.addControl(showWorkBtn);
                if(CmMainPanel.__lastInstance != null)
                    CmMainPanel.__lastInstance._mainContent.layout();
                
                try {
                    /** Show Work is not required, then do not show the ShowWorkRequired
                     * 
                     */
                    if(!UserInfo.getInstance().isShowWorkRequired())
                        hasShowWork=true;
                    
                   // layout();
                   
                   boolean shouldExpandSolution=false;
                   if(UserInfo.getInstance().isAutoTestMode()) {
                       shouldExpandSolution=true;
                       showWorkDialog();
                   }
                   ResourceViewerImplTutor.initializeTutor(pid,resource.getTitle(),hasShowWork,shouldExpandSolution);
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMathTools.showAlert(e.getMessage());
                }
                
                // layout();
            }
        });
    }

    /** Notified whenever a showwork entry is made 
     * 
     * @param pid
     */
    protected void whiteBoardHasBeenUpdated(String pid) {
        if(!this.hasShowWork && this.pid.equals(pid)) {
            // this solution's whiteboard has been updated, so
            // we must make sure the ForceShowWork button is removed
            initializeTutor(pid,this.resource.getTitle(),true,false);
            
            hasShowWork = true;
        }
    }
    
    /** publish native method to allow for opening of Show Window 
     * from external JS using current instance
     * 
     */
    static private native void publishNative() /*-{
        $wnd.showWorkDialog_Gwt = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::showWorkDialog();
    }-*/;
    
    
    /** Called from the show-work-button display on the tutor and default in tutor_wrapper.vm
     * 
     * @TODO: Make this an instance var.  JSNI does not seem to 
     * be working when specified as instance (using this).  Does not 
     * work in hosted mode (does in web mode)
     */
    static public void showWorkDialog() {
       _instance.showWork(_instance.pid);
    }    
    
    public void showWork(final String pid) {
        
        ShowWorkPanel showWorkPanel = new ShowWorkPanel();
        showWorkPanel.setupForPid(pid);
        
        _tabShowWork.removeAll();
        _tabShowWork.setLayout(new FlowLayout());
        Button showSolution = new Button("Show Solution");
        showSolution.setToolTip("View the solution");
        showSolution.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                _cardLayout.setActiveItem(_tabSolution);
            }
        });
        _tabShowWork.add(showSolution);        
        _tabShowWork.add(showWorkPanel);

        _cardLayout.setActiveItem(_tabShowWork);
    }

    
    /**
     * Call specialized JavaScript defined in main js
     * 
     * @param pid
     */
    static private native void initializeTutor(String pid, String title, boolean hasShowWork,boolean shouldExpandSolution) /*-{
                                 $wnd.doLoad_Gwt(pid, title,hasShowWork,shouldExpandSolution);
                                 }-*/;

}
