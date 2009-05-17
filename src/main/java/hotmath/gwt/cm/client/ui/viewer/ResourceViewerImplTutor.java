package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmMainPanel;

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
    public ResourceViewerImplTutor() {
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

    static Window showWorkWin;
    public void removeResourcePanel() {
        if (showWorkWin != null) {
            showWorkWin.hide();
            layout();
        }
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
                addResource(htmlO,resource.getTitle());
                Button showWorkBtn = new Button("Show Work");
                showWorkBtn.setStyleName("show-work-button");
                showWorkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    public void componentSelected(ButtonEvent ce) {
                        showWork(pid);
                    }
                });
              
                CmMainPanel.__lastInstance._mainContent.addControl(showWorkBtn);
                CmMainPanel.__lastInstance._mainContent.layout();
                try {
                    ResourceViewerImplTutor.initializeTutor(pid);
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMath.showAlert(e.getMessage());
                }
            }
        });
                

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
    static private native void initializeTutor(String pid) /*-{
    						     $wnd.doLoad_Gwt(pid);
    						     }-*/;
}
