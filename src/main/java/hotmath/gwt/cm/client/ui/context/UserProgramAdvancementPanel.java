package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Create an embed-able panel that shows the requirements
 * the current user has to complete before advancing 
 * through the program.
 *  
 * @author casey
 *
 */
public class UserProgramAdvancementPanel extends FlowLayoutContainer {
	
	Window window;
	
	public UserProgramAdvancementPanel(PrescriptionData pdata, Window w) {
		this.window = w;
		setStyleName("user-program-advancement-panel");
		PrescriptionSessionData session = pdata.getCurrSession();
		List<PrescriptionSessionDataResource> resources = session.getInmhResources();
		
		PrescriptionSessionDataResource rppRpa=null;
		for(PrescriptionSessionDataResource r: resources) {
			if(r.getType().equals(CmResourceType.PRACTICE)) {
				rppRpa = r;
				break;
			}
		}
		
		ButtonBar hp = new ButtonBar();
		hp.setSpacing(5);
		hp.setStyleName("prog-advance-options");
		/** create a list of items that have not been completed
		 * 
		 */
		int pos=0;
		for(InmhItemData id: rppRpa.getItems()) {
			if(!id.isViewed()) {
				final int ordinal=pos;
				hp.add(new TextButton(id.getTitle(),new SelectHandler() {
				    @Override
				    public void onSelect(SelectEvent event) {
						if(window != null) {
							window.hide();
						}
						CmHistoryManager.loadResourceIntoHistory(CmResourceType.PRACTICE.label(),Integer.toString(ordinal));
					}
				}));
			}
			pos++;
		}
		
		
		/** all complete, ready to move to next lesson */
		int leftToDo = hp.getWidgetCount();
		if(leftToDo == 0) {
			String message = "<p>All requirements have now been completed for this lesson.</p>" +
			                 "<p>You may now move to the next lesson or continue to explore this lesson's resources.</p>";
			add(new HTML(message));
		}
		else {
			String tag="";
			if(leftToDo==1)
				tag = (session.isSessionRpa()?"activity":"problem");
			else 
				tag = (session.isSessionRpa()?"activies":"problems");
			
			add(new HTML("<p>You still need to complete the following " + tag + 
					     " before moving to the next lesson.</p>"));
			add(hp);
		}
	}

}
