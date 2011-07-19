package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage.BackAction;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractPagePanel extends Composite {
	
    protected GenericContainerTag listItems = new GenericContainerTag("ul");



	/** Default backaction.  Sub classes can override
	 * to provide custom back actions.
	 * 
	 * @return
	 */
	public BackAction getBackAction() {
		return null;
	}

	
    
    protected void resetListSelections() {
    	Iterator<Widget> it = listItems.iterator();
    	while(it.hasNext()) {
    		Widget w = it.next();
    		if(w instanceof GenericTextTag<?>) {
    			((GenericTextTag<String>)w).removeStyleName("is_selected");
    		}
    	}
    }

}