package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Html;

/** Shown when a RPP/RPA is completed 
 * 
 * @author casey
 *
 */
public class RequiredPracticeCompleteDialog extends CmWindow {
	
	public RequiredPracticeCompleteDialog(String title, String msg) {
		EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));

		setStyleName("required-practice-complete-dialog");
		setSize(330, 210);
		setModal(true);
		setHeading(title);
		add(new Html("<p style='font-weight: bold;'>" + msg + "</p>"));
		add(new UserProgramAdvancementPanel(PrescriptionResourcePanel.__instance.pData, this));
		
		setClosable(true);
		addCloseButton();
		addWindowListener(new WindowListener() {
			@Override
			public void windowHide(WindowEvent we) {
				EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
			}
		});
		
		setVisible(true);
	}
	
	
}
