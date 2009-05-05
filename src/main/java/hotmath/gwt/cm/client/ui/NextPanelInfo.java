package hotmath.gwt.cm.client.ui;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

/** Defines info needed for the DoNext panel
 * 
 * @author Casey
 *
 */
public interface NextPanelInfo {
	
	/** Get the widget to display in the Do Next dialog
	 * 
	 * @return
	 */
	public Widget getNextPanelWidget();
	
	/** Return list of actions to provide in the Do Next dialog
	 * 
	 * @return
	 */
	public List<Widget> getNextPanelActions();
	
	
	/** Return the tooltip used in the 'next' button on the header
	 * 
	 * @return
	 */
	public String getNextPanelToolTip();
	
	
	/** Do what ever is to be done next
	 * 
	 */
	public void doNext();
}
