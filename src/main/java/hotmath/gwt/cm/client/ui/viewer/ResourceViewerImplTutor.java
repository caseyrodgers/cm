package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmMainPanel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends ResourceViewerContainer implements ResourceViewer {
    public ResourceViewerImplTutor() {
        addStyleName("resource-viewer-impl-tutor");
        
        setScrollMode(Scroll.NONE);  // iframe in TabPanel need to do scrolling
    }

    Button showWorkBtn, hideWorkBtn;
    String pid;
    InmhItemData resource;
    TabPanel _tabPanel;
    TabItem _solutionTabItem;
    
    public Widget getResourcePanel(final InmhItemData resource) {
        this.pid = resource.getFile();
        this.resource = resource;

        _tabPanel = new TabPanel();
        addResource(_tabPanel,resource.getTitle());

        // setup tab to allow for async access to solution panel
        TabPanel panel = new TabPanel();
        panel.setResizeTabs(true);
        panel.setAnimScroll(true);
        
        showWork(pid);        
       
        _solutionTabItem = new TabItem();
        
        _solutionTabItem.setClosable(false);
        _solutionTabItem.setText("Solution");
        _tabPanel.add(_solutionTabItem);          
        
        
        _tabPanel.addListener(Events.Select,new Listener() {
            public void handleEvent(BaseEvent be) {
                String t = _tabPanel.getSelectedItem().getText();
                if(t.equals("Solution")) {
                    if(_solutionTabItem.getItems().size() == 0) {
                        showSolution();
                        CmMainPanel.__lastInstance._mainContent.resetChildSize();                        
                    }
                }
            }
        });
        
        
        return this;
    }

    ContentPanel showWorkWin;

    public void removeResourcePanel() {
        if (showWorkWin != null) {
            showWorkWin.hide();
            showWorkWin.removeFromParent();
            showWorkWin = null;
            layout();
        }
    }


    /** Called when the resource viewer is resized during window
     *  change event.
     *  
     *  We need to catch this to resize the IFRame that contains
     *  the Flash whiteboard.
     *  
     */
    public void setResourceContinerHeight(int height) {
        if(showWorkPanel != null) {
            showWorkPanel.setHeight(height + "px");
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
                
                _solutionTabItem.removeAll();
                _solutionTabItem.add(new HTML(html));
                try {
                    ResourceViewerImplTutor.initializeTutor(pid);
                    layout();
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMath.showAlert(e.getMessage());
                }
            }
        });
                

    }

    ShowWorkPanel showWorkPanel;
    public void showWork(final String pid) {
        
        showWorkPanel = new ShowWorkPanel();
        showWorkPanel.setupForPid(pid);
        
        TabPanel panel = new TabPanel();
        panel.setResizeTabs(true);
        panel.setAnimScroll(true);
       
        TabItem item = new TabItem();
        item.setClosable(false);
        item.setText("Show Work");
        item.add(showWorkPanel);
        
        _tabPanel.add(item);
        
        layout();
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
