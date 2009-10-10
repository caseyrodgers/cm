package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
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
    
    public ResourceViewerImplTutor() {
        _instance = this;
        addStyleName("resource-viewer-impl-tutor");
        setScrollMode(Scroll.AUTOY);
        
        setNoHeaderOrFooter();
    }

    Button showWorkBtn, hideWorkBtn;
    String pid;
    boolean hasShowWork;
    InmhItemData resource;
    public Widget getResourcePanel(final InmhItemData resource) {
        this.item = resource;
        this.pid = resource.getFile();
        this.resource = resource;
        showSolution();
        
        return this;
    }

    static CmWindow showWorkWin;
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
    
    public String getPid() {
        return this.pid;
    }
    
    /** Load the tutor 
     * 
     */
    public void showSolution() {
        
        Log.debug("ResourceViewerImplTutor: loading solution '" + pid + "'");
        
        // call for the solution HTML
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new GetSolutionAction(UserInfo.getInstance().getUid(), pid), new AsyncCallback<RpcData>() {
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }

            public void onSuccess(RpcData result) {
                String html = result.getDataAsString("solutionHtml");
                boolean hasShowWork = result.getDataAsInt("hasShowWork") > 0;
                
                Html htmlO = new Html(html);
                htmlO.setStyleName("tutor_solution_wrapper");

                addResource(htmlO,resource.getTitle());
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
                    
                   layout();
                   
                   boolean shouldExpandSolution=false;
                   if(UserInfo.getInstance().isAutoTestMode()) {
                       shouldExpandSolution=true;
                       //showWorkDialog();
                   }
                   ResourceViewerImplTutor.initializeTutor(resource.getFile(),resource.getTitle(),hasShowWork,shouldExpandSolution);
                   
                   
                   EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_SOLUTION_SHOW,resource));
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMathTools.showAlert(e.getMessage());
                }
                
                layout();
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
        $wnd.showTutoringDialog_Gwt = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor::showTutoringDialog();
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
    
    
    static public void showTutoringDialog() {
        _instance.showTutoring(_instance.pid);
     }    
    


    static ShowWorkPanel showWorkPanel;
    public void showWork(final String pidIn) {
        if(showWorkWin == null) {
            showWorkWin = new CmWindow();
            showWorkWin.setClosable(false);
            showWorkWin.setResizable(false);

            
            Button viewSolution = new Button("View solution");
            viewSolution.setToolTip("View the tutorial to check your answer");
            viewSolution.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    //CatchupMathTools.showAlert("Pid: " + _pid);
                    initializeTutor(showWorkPanel.getPid(),_instance.resource.getTitle(),hasShowWork,true);
                    showWorkWin.hide();
                }
            });
            showWorkWin.setHeading("Enter your answer on the whiteboard");

            
            // [Back] [View Solution] [Examples] [Clear]
            Button back = new Button("Back");
            back.setToolTip("Return back to the solution");
            back.addSelectionListener(new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    showWorkWin.hide();
                }
            });
            
            Button examples = new Button("Examples");
            examples.setToolTip("View sample uses of Show Work");
            examples.addSelectionListener(new SelectionListener<ButtonEvent>() {
                
                @Override
                public void componentSelected(ButtonEvent ce) {
                    showWorkWin.hide();  // must hide flash component to show popup
                    new ShowWorkExampleWindow();
                }
            });
            
//            Button clear = new Button("Clear");
//            clear.setToolTip("Clear the Show Work panel");            
//            clear.addSelectionListener(new SelectionListener<ButtonEvent>() {
//                @Override
//                public void componentSelected(ButtonEvent ce) {
//                    CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
//                    s.execute(new ClearWhiteboardDataAction(UserInfo.getInstance().getUid(),UserInfo.getInstance().getRunId(), pid), new AsyncCallback<RpcData>() {
//                        @Override
//                        public void onSuccess(RpcData result) {
//                           // initialize the 
//                            showWorkPanel.clearWhiteBoard();
//                        }
//                        @Override
//                        public void onFailure(Throwable caught) {
//                            caught.printStackTrace();
//                        }
//                    });
//                }
//            });
            


            showWorkWin.getHeader().addTool(viewSolution);
            showWorkWin.getHeader().addTool(examples);
            showWorkWin.getHeader().addTool(back);            
//            showWorkWin.getHeader().addTool(clear);
            
            int left = ResourceViewerImplTutor.this.el().getLeft(false);
            int top = ResourceViewerImplTutor.this.el().getTop(false);

            showWorkWin.setPosition(left,top);

            showWorkWin.setStyleName("show-work-window");
            showWorkWin.setScrollMode(Scroll.NONE);
            
            showWorkWin.setHeight(540);
            showWorkWin.setWidth(560);
            showWorkWin.setModal(true);

                    
            showWorkWin.setLayout(new FillLayout());
        }
        else {
            showWorkWin.remove(showWorkPanel);
        }
        
        showWorkPanel = new ShowWorkPanel();
        showWorkPanel.setupForPid(pid);
        showWorkWin.add(showWorkPanel);

        showWorkWin.setVisible(true);
        // get the position of the 'show work' button
        // and move to it, then expand ...
    }
    
    
    
    /** Display LWL tutoring in separate browser window.
     * 
     * It does not play well in a DHTML controlled window
     * 
     * 
     * @param pid
     */
    public void showTutoring(String pid) {

        
        if(!UserInfo.getInstance().isTutoringAvail()) {
            CatchupMathTools.showAlert("Ask a Tutor", "Live Tutoring is not currently enabled on this account.");
            return;
        }
        
        String contentUrl = "pid=" + pid;
        String url = "/collab/lwl/cm_lwl_launch.jsp?uid=" + UserInfo.getInstance().getUid() + "&contentUrl=" + contentUrl;
    
        int w = 800;
        int h = 560;
        String windowProps = "toolbar=0, titlebar=0, status=0, menubar=0, resizable=0, width=" + w + 
                             ", height=" + h + ", directories=0, location=0,scrollbars=0,directories=0,location=0";

        com.google.gwt.user.client.Window.open(url, "_blank", windowProps);
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



/** Display a few examples of using the Show Work 
 *   as a DHTML window.
 *   
 * @author casey
 *
 */
class ShowWorkExampleWindow extends Window {
    
    public ShowWorkExampleWindow() {
        setStyleName("show-work-example-window");
        setSize(440,480);
        setHeading("Show Work Examples");
        Frame frame = new Frame("/gwt-resources/show_work_examples.html");
        DOM.setElementPropertyInt(frame.getElement(), "frameBorder", 0); // disable border
        DOM.setElementProperty(frame.getElement(), "scrolling", "auto"); // disable border
        
        frame.setSize("630px","475px");
        setLayout(new FitLayout());
        add(frame);
        setVisible(true);
    }
}
