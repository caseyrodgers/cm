package hotmath.gwt.cm_mobile_shared.client.util;

import hotmath.gwt.cm_mobile_shared.client.event.LoadingSpinner;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEventHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

public class LoadingDialog extends DialogBox  {
	
    EventBus eventBus;
    LoadingSpinner spinner;
	public LoadingDialog(EventBus eventBus) {
	    super(false,false);
	    addStyleName("loadingDialog");
	    this.eventBus = eventBus;
		setSize("150px", "50px");
		setGlassEnabled(false);
		
		setWidget(new HTML("<div id='spinner'>&nbsp;</div>"));
		setAnimationEnabled(false);
		setAutoHideEnabled(false);
		
		eventBus.addHandler(SystemIsBusyEvent.TYPE, new SystemIsBusyEventHandler() {
		    @Override
		    public void showIsBusy(boolean trueFalse) {
		        //trueFalse=true;
		        if(trueFalse) {
	                if(spinner != null) {
	                    spinner.startSpinner();
	                }

		            showCentered();
		        }
		        else {
		            if(spinner != null) {
		                spinner.stopSpinner();
		            }
		        }
		        
		        setVisible(trueFalse);
		    }
		});
		
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                spinner = new LoadingSpinner(getElement(),"spinner");
            }
        });
		
	}
	
	public void showCentered() {
		center();
	}
}
