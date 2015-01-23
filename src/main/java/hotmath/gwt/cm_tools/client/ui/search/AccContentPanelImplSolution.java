package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.ui.ShowWhiteboardWindow;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorer.TopicExplorerCallback;
import hotmath.gwt.cm_tools.client.ui.viewer.CalculatorWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class AccContentPanelImplSolution extends AccContentPanel {

    public AccContentPanelImplSolution(PrescriptionSessionDataResource resource, TopicExplorerCallback callback) {
        super(resource, callback);
    }

    private ResourceViewerImplTutor2 _viewer;

    @Override
    protected void showResource(CmResourcePanel viewer, String title, boolean b) {
        super.showResource(viewer, title, b);
        final ResourceViewerImplTutor2 _viewer = (ResourceViewerImplTutor2) viewer;
    }

    /**
     * Remove all whiteboard buttons
     * 
     */
    @Override
    public List<Widget> getContainerTools(CmResourcePanel viewer) {
        List<Widget> tools = super.getContainerTools(viewer);
        List<Widget> newTools = new ArrayList<Widget>();
        
        /** remove default whiteboard actions */
        for (int i = 0; i < tools.size(); i++) {
            Widget w = tools.get(i);
            if (w instanceof TextButton) {
                if (((TextButton) w).getValue().indexOf("Whiteboard") == 0) {
                    newTools.add(w);
                }
            }
        }

        TextButton whiteboardBtn = new TextButton("Whiteboard", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                ShowWhiteboardWindow.getInstance().setVisible(true);
            }
        });

        TextButton calcBtn = new TextButton("Calculator", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CalculatorWindow.getInstance().setVisible(true);
            }
        });

        newTools.add(whiteboardBtn);
        newTools.add(calcBtn);
        
        return newTools;
    }
}
