package hotmath.gwt.cm.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

/** Default Next Panel Info
 * 
 * @author Casey
 *
 */
abstract public class NextPanelInfoImplDefault implements NextPanelInfo {

	List<Widget> actions = new ArrayList<Widget>();

	abstract public void doNext();
	abstract public Widget getNextPanelWidget();
	
	public List<Widget> getNextPanelActions() {
		return actions;
	}

	public String getNextPanelToolTip() {
		return null;
	}
}
