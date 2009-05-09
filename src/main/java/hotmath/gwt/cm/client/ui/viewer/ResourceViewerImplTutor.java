package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.ui.CmMainPanel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplTutor extends ResourceViewerContainer implements ResourceViewer {
    public ResourceViewerImplTutor() {
        addStyleName("resource-viewer-impl-tutor");
    }

    Button showWorkBtn=null;
    
    public Widget getResourcePanel(final InmhItemData resource) {
        final String pid = resource.getFile();

        showWorkBtn = new Button("Show Work");
        showWorkBtn.setStyleName("show-work-button");
        showWorkBtn.setTitle("Display the Show Work area for this solution");
        showWorkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showWork(pid);
            }
        });

        // call for the solution HTML
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getSolutionHtml(pid, new AsyncCallback() {
            public void onFailure(Throwable caught) {
                CatchupMath.showAlert(caught.getMessage());
            }

            public void onSuccess(Object result) {
                String html = (String) result;
                addResource(new HTML(html), resource.getTitle());
                CmMainPanel.__lastInstance._mainContent.addControl(showWorkBtn);
                try {
                    ResourceViewerImplTutor.initializeTutor(pid);
                    // for debugging
                    // showWork(pid);
                    layout();
                } catch (Exception e) {
                    e.printStackTrace();
                    CatchupMath.showAlert(e.getMessage());
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

    public void showWork(final String pid) {

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
