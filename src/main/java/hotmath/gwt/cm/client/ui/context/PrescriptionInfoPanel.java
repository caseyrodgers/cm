package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.ui.CmContext;
import hotmath.gwt.cm.client.ui.ContextChangeListener;
import hotmath.gwt.cm.client.ui.ContextController;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Label;

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

        String html = "<p>Use the Help button to change your wallpaper.</p>" +
                      "<p>Please view all steps of each required practice problem answers before advancing.</p>";
        Html htmlO = new Html(html);
        add(htmlO);
        
        ContextController.getInstance().addContextChangeListener(new ContextChangeListener() {
            public void contextChanged(CmContext context) {
                updateStatus(context);
            }
        });
    }
    
    public void updateStatus(CmContext context) {
        setToolTip(context.getStatusMessage());
        
        
        //_labStatus.setText(context.getStatusMessage());
        //layout();
    }
    
}