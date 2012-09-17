package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.ContextChangeListener;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/** The information about the user's current state in the program
 * 
 * @author casey
 *
 */
class PrescriptionInfoPanel extends FlowLayoutContainer {
    PrescriptionCmGuiDefinition cmDef;
    public PrescriptionInfoPanel(PrescriptionCmGuiDefinition cm) {
        cmDef = cm;
        setStyleName("prescription-info-panel");

        String html = "<p>Use any menu items you wish, before or after trying " +
                      "the Required Practice problems.</p>";
        HTML htmlO = new HTML(html);
        add(htmlO);
        
        add(new HTML(CatchupMathTools.FEEDBACK_MESSAGE));
        
        ContextController.getInstance().addContextChangeListener(new ContextChangeListener() {
            public void contextChanged(CmContext context) {
                updateStatus(context);
            }
        });
    }
    
    public void updateStatus(CmContext context) {
        //setToolTip(context.getStatusMessage());
        //_labStatus.setText(context.getStatusMessage());
        //layout();
    }
    
}