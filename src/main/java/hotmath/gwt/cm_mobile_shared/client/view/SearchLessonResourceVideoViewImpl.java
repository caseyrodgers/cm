package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile3.client.ui.HeaderPanel;


public class SearchLessonResourceVideoViewImpl extends PrescriptionLessonResourceVideoViewImpl {
    
    @Override
    public String getViewTitle() {
        return "Search Video";
    }
    
    
    @Override
    public String getHeaderBackground() {
        return HeaderPanel.BACKGROUND_SEARCH;
    }
    
    
    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }

}
