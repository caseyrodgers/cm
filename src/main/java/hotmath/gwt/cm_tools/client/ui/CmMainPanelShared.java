package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

public class CmMainPanelShared extends BorderLayoutContainer {

    int WAIT_TIME = 1; // instantaneous 

    /** Force GXT/GWT to update UI!
     *  normal forceLayout does not seem 
     *  to work with child panels are initialized
     *  after visible (ie, flash objects/games ..etc..)
     *  
     *  
     */
    public void makeSureUiIsRefreshed(final CmResourcePanel panel) {
        if(panel.needForcedUiRefresh()) {
            panel.getResourcePanel().setVisible(false);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    panel.getResourcePanel().setVisible(true);
                    forceLayout();
                }
            });
        }
        else {
            forceLayout();
        }
    }
   

}
