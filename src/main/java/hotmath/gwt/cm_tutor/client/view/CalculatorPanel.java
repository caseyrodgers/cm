package hotmath.gwt.cm_tutor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/** Create a wrapper around extern jquery calculator 
 * 
 *  This is external to GXT and is shared amongst tools.
 * @author casey
 *
 */

public class CalculatorPanel extends Composite {
    
    interface MyUiBinder extends UiBinder<Widget, CalculatorPanel> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    public CalculatorPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                setupJsni();
            }
        });
    }
    
    private native void  setupJsni() /*-{
        $wnd.requireJsLoad_calculator('calculator-container',
            function(calc) {
                $wnd.console.log("CalculatorPanel is ready");
            }
        );
    
    }-*/;
    
}
