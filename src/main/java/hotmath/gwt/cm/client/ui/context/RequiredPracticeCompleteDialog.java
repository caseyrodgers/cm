package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

/** Shown when a RPP/RPA is completed 
 * 
 * @author casey
 *
 */
public class RequiredPracticeCompleteDialog extends GWindow {
	
	public RequiredPracticeCompleteDialog(String title, String msg) {
	    super(true);
	    
		EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));

		setStyleName("required-practice-complete-dialog");
		setPixelSize(330, 210);
		setModal(true);
		setHeadingText(title);
		
		VerticalLayoutContainer vert = new VerticalLayoutContainer();
		
		vert.add(new HTML("<p style='font-weight: bold;'>" + msg + "</p>"));
		vert.add(new UserProgramAdvancementPanel(PrescriptionResourcePanel.__instance.pData, this));
		
		setWidget(vert);
		
		addHideHandler(new HideHandler() {
		    @Override
		    public void onHide(HideEvent event) {
				EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
			}
		});
		
		setVisible(true);
	}
	
	
}
