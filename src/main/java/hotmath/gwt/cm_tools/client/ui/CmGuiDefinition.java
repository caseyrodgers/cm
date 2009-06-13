package hotmath.gwt.cm_tools.client.ui;




import hotmath.gwt.cm_tools.client.ui.context.CmContext;

import com.google.gwt.user.client.ui.Widget;

/** A single resource that is shown in the resource type 
 *  are of the main panel.
 *  
 *  
 * @author Casey
 *
 */
public interface CmGuiDefinition {
	
	/** Get the title to display for this resource type
	 * 
	 * @return
	 */
	String getTitle();
	
	/** Get the widget used to identify this resource in 
	 *  the west side of main app area.
	 * @return
	 */
	Widget getWestWidget();
	
	
	/** Return the widget (if any ) that makes up the main 
	 *  app area for this resource
	 *  
	 * @return
	 */
	Widget getCenterWidget();
	
	
	/** Return the current context information about this widget.
	 * 
	 * This will describe current configuration issues that bind the 
	 * west and center gui objects.
	 * 
	 * 
	 * @return
	 */
	CmContext getContext();
}
