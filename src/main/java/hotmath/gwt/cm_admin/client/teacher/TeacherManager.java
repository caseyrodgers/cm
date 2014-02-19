package hotmath.gwt.cm_admin.client.teacher;

import hotmath.gwt.cm_tools.client.ui.GWindow;

public class TeacherManager extends GWindow {
    
    public TeacherManager() {
        super(true);
        
        setHeadingText("Teacher Manager");
        setModal(true);
        setPixelSize(500, 300);
        
        buildUi();
    }
    
    private void buildUi() {
        
    }

}
