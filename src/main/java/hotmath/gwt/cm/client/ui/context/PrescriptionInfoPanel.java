package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.ContextChangeListener;
import hotmath.gwt.cm.client.ui.ContextController;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

/** The information about the user's current state in the program
 * 
 * @author casey
 *
 */
class PrescriptionInfoPanel extends LayoutContainer {
    PrescriptionCmGuiDefinition cmDef;
    public PrescriptionInfoPanel(PrescriptionCmGuiDefinition cm) {
        cmDef = cm;
        setStyleName("prescription-info-panel");
        ContextController.getInstance().addContextChangeListener(new ContextChangeListener() {
            public void contextChanged(CmContext context) {
                updateStatus(context);
            }
        });
    }
    
    public void updateStatus(CmContext context) {
        setToolTip(context.getStatusMessage());
    }
    
}