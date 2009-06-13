package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StudentShowWorkPanel extends ContentPanel {
    
    public StudentShowWorkPanel() {
        setLayout(new BorderLayout());
        add(createWestPanel(), new BorderLayoutData(LayoutRegion.WEST,200));
        add(createCenterPanel(), new BorderLayoutData(LayoutRegion.CENTER));
    }
    
    private Widget createWestPanel() {
        return new Label("WEST");
    }
    
    private Widget createCenterPanel() {
        return new Label("CENTER");
    }

}
