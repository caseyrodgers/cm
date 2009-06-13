package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends ResourceViewerContainer implements ResourceViewer {
    static ResourceViewerImplTutor _instance;
    static {
        publishNative();
    }
    
    public ResourceViewerImplTutor() {
        _instance = this;
        addStyleName("resource-viewer-impl-tutor");
        setScrollMode(Scroll.AUTOY);
        
        setNoHeaderOrFooter();
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
        
        // call for the solution HTML
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getSolutionHtml(pid, new AsyncCallback() {
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }

            public void onSuccess(Object result) {
                String html = (String) result;
                
                Html htmlO = new Html(html);
                htmlO.setStyleName("tutor_solution_wrapper");

                addResource(htmlO,resource.getTitle());
                setNoHeaderOrFooter();
              
                //CmMainPanel.__lastInstance._mainContent.addControl(showWorkBtn);
                if(CmMainPanel.__lastInstance != null)
                    CmMainPanel.__lastInstance._mainContent.layout();
                

                try {
                    ResourceViewerImplTutor.initializeTutor(pid,resource.getTitle());
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMathTools.showAlert(e.getMessage());
                }
                
                layout();
            }
        });
                

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
    
    static ShowWorkPanel showWorkPanel;
    public void showWork(final String pid) {
        if(showWorkWin == null) {
            showWorkWin = new Window();
            Button hideBtn = new Button("Hide");
            hideBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    showWorkWin.hide();
                }
            });
            showWorkWin.setHeading("Show Your Work");
            showWorkWin.getHeader().addTool(hideBtn);
            showWorkWin.setStyleName("show-work-window");
            showWorkWin.setScrollMode(Scroll.NONE);
            
            showWorkWin.setHeight(420);
            showWorkWin.setWidth(560);
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

    
    /**
     * Call specialized JavaScript defined in main js
     * 
     * @param pid
     */
    static private native void initializeTutor(String pid, String title) /*-{
                                 $wnd.doLoad_Gwt(pid, title);
                                 }-*/;

}
