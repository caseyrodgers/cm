package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.GWindow;

public class CustomProblemManagerDialog extends GWindow {
    
    private int adminId;

    public CustomProblemManagerDialog(int adminId) {
        super(true);
        
        setHeadingHtml("Custom Problem Manager");
        this.adminId = adminId;
        
        setVisible(true);
    }

}
