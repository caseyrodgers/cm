package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmMainPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends LayoutContainer implements ResourceViewer {
    
    static ResourceViewerImplTutor _instance;
    
    static {
        publishNative();
    }
    public ResourceViewerImplTutor() {
        
        _instance = this;
        addStyleName("resource-viewer-impl-tutor");
        setScrollMode(Scroll.AUTO);  // iframe in TabPanel need to do scrolling
    }

    Button showWorkBtn, hideWorkBtn;
    String pid;
    InmhItemData resource;
    public Widget getResourcePanel(final InmhItemData resource) {
        this.pid = resource.getFile();
        this.resource = resource;
        showSolution();
        
        return this;
    }

    public void removeResourcePanel() {
    }
    
    /** Load the tutor 
     * 
     */
    public void showSolution() {
        
        // call for the solution HTML
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getSolutionHtml(pid, new AsyncCallback() {
            public void onFailure(Throwable caught) {
                CatchupMath.showAlert(caught.getMessage());
            }

            public void onSuccess(Object result) {
                String html = (String) result;
                
                Html htmlO = new Html(html);
                htmlO.setStyleName("tutor_solution_wrapper");
                
                add(htmlO);
                
                CmMainPanel.__lastInstance._mainContent.addControl(showWorkBtn);
                CmMainPanel.__lastInstance._mainContent.layout();
                try {
                    ResourceViewerImplTutor.initializeTutor(pid, resource.getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMath.showAlert(e.getMessage());
                }
            }
        });
    }

    /** Called from the show-work-button display on the tutor and default in tutor_wrapper.vm
     * 
     * @TODO: Make this an instance var.  JSNI does not seem to 
     * be working when specified as instance (using this).  Does not 
     * work in hosted mode (does in web mode)
     */
    static public void showWorkDialog() {
       _instance.showWork(_instance.pid);
    }
    /** publish native method to allow for opening of Show Window 
     * from external JS using current instance
     * 
     */
    static private native void publishNative() /*-{
        $wnd.showWorkDialog_Gwt = @hotmath.gwt.cm.client.ui.viewer.ResourceViewerImplTutor::showWorkDialog();
    }-*/;
    
    boolean showWorkActive;
    public void showWork(final String pid) {

        if(showWorkActive) {
            Log.debug("ResourceViewerImplTutor: show work active");
            return;
        }
        
        
        Log.debug("ResourceViewerImplTutor: showing ShowWork window");
        
        final ContentPanel showWorkWin = new ContentPanel();
        Button hideBtn = new Button("Hide");
        hideBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showWorkWin.hide();
                CmMainPanel.__lastInstance._mainContent.remove(showWorkWin);
                CmMainPanel.__lastInstance._mainContent.layout();
                
                showWorkActive=false;
            }
        });
        showWorkWin.setHeading("Show Your Work");
        showWorkWin.getHeader().addTool(hideBtn);
        showWorkWin.setScrollMode(Scroll.NONE);
        
        showWorkWin.setHeight(420);
        showWorkWin.setWidth(560);
        showWorkWin.setLayout(new FillLayout());
        
        ShowWorkPanel showWorkPanel = new ShowWorkPanel();
        showWorkPanel.setupForPid(pid);
        showWorkWin.add(showWorkPanel);
        
        
        ResourceViewerImplTutor.initializeTutor(pid, resource.getTitle());
        
        
        CmMainPanel.__lastInstance._mainContent.add(showWorkWin);
        CmMainPanel.__lastInstance._mainContent.layout();

        showWorkActive=true;
        // get the position of the 'show work' button
        // and move to it, then expand ...
    }

    /**
     * Call specialized JavaScript defined in main js
     * 
     * @param pid
     */
    static private native void initializeTutor(String pid, String title) /*-{
    						     $wnd.doLoad_Gwt(pid, title);
    						     }-*/;

    @Override
    public String getContainerStyleName() {
        // TODO Auto-generated method stub
        return null;
    }
}
