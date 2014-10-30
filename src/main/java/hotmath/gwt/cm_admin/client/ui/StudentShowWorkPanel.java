package hotmath.gwt.cm_admin.client.ui;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class StudentShowWorkPanel extends ContentPanel {
    
    public StudentShowWorkPanel() {

    	BorderLayoutContainer blContainer = new BorderLayoutContainer();

    	BorderLayoutData blData = new BorderLayoutData(200);
    	blContainer.setWestWidget(createWestPanel(), blData);
    	blContainer.setCenterWidget(createCenterPanel(), blData);
    }
    
    private Widget createWestPanel() {
        return new Label("WEST");
    }
    
    private Widget createCenterPanel() {
        return new Label("CENTER");
    }

}
