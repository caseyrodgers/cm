package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourceContentPanel.ResourceViewerState;

import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplQuiz extends CmResourcePanelImplDefault {

    public Widget getResourcePanel() {
        return new QuizPage(true,null);
    }
    
    @Override
    public ResourceViewerState getInitialMode() {
        // TODO Auto-generated method stub
        return ResourceViewerState.MAXIMIZED;
    }
}
