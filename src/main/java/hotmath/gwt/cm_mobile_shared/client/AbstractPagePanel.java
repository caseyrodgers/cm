package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage.BackAction;

import com.google.gwt.user.client.ui.Composite;

public abstract class AbstractPagePanel extends Composite {

	/** Default backaction.  Sub classes can override
	 * to provide custom back actions.
	 * 
	 * @return
	 */
	public BackAction getBackAction() {
		return null;
	}

}