package hotmath.gwt.cm_admin.client.ui;

import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

public class FeatureNotAvailableToMobile extends AlertMessageBox {
    
    public FeatureNotAvailableToMobile() {
        super("Feature Not Available", "This feature is not available on mobile devices.  Please visit catchupmath.com from your desktop.");
        setVisible(true);
    }

}
