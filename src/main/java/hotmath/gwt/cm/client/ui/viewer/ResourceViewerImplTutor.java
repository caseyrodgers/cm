package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends ResourceViewerContainer implements ResourceViewer {
    public ResourceViewerImplTutor() {
        addStyleName("resource-viewer-impl-tutor");
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
        
//        hideWorkBtn = new Button("Hide Work");
//        hideWorkBtn.setStyleName("hide-work-button");
//        hideWorkBtn.setTitle("Hide the Show Work area for this solution");
//        hideWorkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
//            public void componentSelected(ButtonEvent ce) {
//                showWork();
//           }
//        });
//
//        showWorkBtn = new Button("Show Work");
//        showWorkBtn.setStyleName("show-work-button");
//        showWorkBtn.setTitle("Display the Show Work area for this solution");
//        showWorkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
//            public void componentSelected(ButtonEvent ce) {
//                showWork(pid);
//           }
//        });

        

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
                    if(_solutionTabItem.getItems().size() == 0)
                        showSolution();
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

    public void showWork(final String pid) {
        
        ShowWorkPanel showWork1 = new ShowWorkPanel();
        showWork1.setupForPid(pid);
        
        TabPanel panel = new TabPanel();
        panel.setResizeTabs(true);
        panel.setAnimScroll(true);
       
        TabItem item = new TabItem();
        item.setClosable(false);
        item.setText("Show Work");
        item.add(showWork1);
        
        _tabPanel.add(item);
        
        layout();
        
        
        if(true)
            return;
        
        
        if (showWorkWin != null) {
            removeResourcePanel();
        }

        showWorkWin = new Window();

        Button viewSolutionBtn = new Button("Close Show Work");
        viewSolutionBtn.setStyleName("view-solution-button");
        viewSolutionBtn.setToolTip("View the solution and work through the problem");
        viewSolutionBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                removeResourcePanel();
            }
        });
        showWorkWin.add(viewSolutionBtn);
        add(showWorkWin);
        showWorkWin.setHeading("Show Work Window");
        showWorkWin.setHeight(365);
        showWorkWin.setWidth(410);
        showWorkWin.setAnimCollapse(true);
        showWorkWin.setCollapsible(true);
        ShowWorkPanel showWork = new ShowWorkPanel();
        showWork.setupForPid(pid);
        showWorkWin.add(showWork);
        showWorkWin.setVisible(true);
        

        

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
