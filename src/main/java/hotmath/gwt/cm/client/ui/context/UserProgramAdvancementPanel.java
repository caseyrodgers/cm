package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.client.history.CmHistoryManager;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;

/** Create an embed-able panel that shows the requirements
 * the current user has to complete before advancing 
 * through the program.
 *  
 * @author casey
 *
 */
public class UserProgramAdvancementPanel extends LayoutContainer {
	
	CmWindow window;
	
	public UserProgramAdvancementPanel(PrescriptionData pdata, CmWindow w) {
		this.window = w;
		setStyleName("user-program-advancement-panel");
		PrescriptionSessionData session = pdata.getCurrSession();
		List<PrescriptionSessionDataResource> resources = session.getInmhResources();
		
		PrescriptionSessionDataResource rppRpa=null;
		for(PrescriptionSessionDataResource r: resources) {
			if(r.getType().equals("practice")) {
				rppRpa = r;
				break;
			}
		}
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setHorizontalAlign(HorizontalAlignment.CENTER);
		hp.setSpacing(5);
		hp.setStyleName("prog-advance-options");
		/** create a list of items that have not been completed
		 * 
		 */
		int pos=0;
		for(InmhItemData id: rppRpa.getItems()) {
			if(!id.isViewed()) {
				final int ordinal=pos;
				hp.add(new Button(id.getTitle(),new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if(window != null) {
							window.close();
						}
						CmHistoryManager.loadResourceIntoHistory("practice",Integer.toString(ordinal));
					}
				}));
			}
			pos++;
		}
		
		/** all complete, ready to move to next lesson */
		int leftToDo = hp.getItemCount();
		if(leftToDo == 0) {
			String message = "<p>All requirements have now been completed for this lesson.</p>" +
			                 "<p>You may now move to the next lesson or continue to explore this lesson's resources.</p>";
			add(new Html(message));
		}
		else {
			String tag="";
			if(leftToDo==1)
				tag = (session.isSessionRpa()?"activity":"problem");
			else 
				tag = (session.isSessionRpa()?"activies":"problems");
			
			add(new Html("<p>You still need to complete the following " + tag + 
					     " before moving to the next lesson.</p>"));
			add(hp);
		}
	}

}
