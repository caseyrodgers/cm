package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.ContextChangeListener;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;

import com.extjs.gxt.ui.client.widget.Html;
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

        String html = "<p>Use the Help button to change your wallpaper.</p>";
        Html htmlO = new Html(html);
        add(htmlO);
        
        add(new Html(CatchupMathTools.FEEDBACK_MESSAGE));
        
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