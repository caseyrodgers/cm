package hotmath.gwt.cm_mobile_assignments.client.util;

import com.google.gwt.user.client.ui.HTML;
import com.googlecode.mgwt.ui.client.dialog.PopinDialog;
import com.googlecode.mgwt.ui.client.widget.ProgressIndicator;

public class AssBusy extends PopinDialog {
    public AssBusy() {
        add(new HTML("<h1>Please wait .. &nbsp;</h1>"));
        add(new ProgressIndicator());
        center();
    }
    
    static private AssBusy __assBusy;
    static public void showBusy(boolean yesNo) {
        if(yesNo) {
            if(__assBusy == null) {
                __assBusy = new AssBusy();
            }
            __assBusy.show();
        }
        else {
            if(__assBusy != null) {
                __assBusy.hide();
                __assBusy.clear();
                __assBusy = null;
            }
        }
    }
}
