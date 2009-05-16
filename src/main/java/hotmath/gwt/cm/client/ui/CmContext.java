package hotmath.gwt.cm.client.ui;


import java.util.List;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.IconButton;


/** A context is a single state of a GUI form.
 * 
 * For example, on the Prescription form as the user
 * selects different resources, each one is a new context.
 * 
 * However, the main gui definition will stay the same.  
 * 
 * @author Casey
 *
 */
public interface CmContext {
	
	/** Reset the system back to this context
	 * 
	 */
	public void resetContext();
	
	
	/** Do what ever is the logical next step for this context
	 * 
	 */
	public void doNext();
	
	
	/** Do what ever is the logical previous step for this context
	 * 
	 */
	public void doPrevious();
	

	/** Return the title to use for this context.
	 * 
	 * This might contain specific context info, such
	 * as current session, session number, etc..
	 * 
	 * @return
	 */
	public String getContextTitle();
	
	
	/** Return String used to display the current
	 *  context's help information.
	 *  
	 *  This is the defined here to allow different
	 *  help texts depending on user's state.
	 *  
	 *  
	 * @return
	 */
	public String getContextHelp();
	
	
	
	/** Return the completion percent of the current 
	 * context as an integer (ie, 60,34, etc)
	 * 
	 * Each time a new context is loaded, this will be
	 * call to allow the context to set its position in 
	 * overall completion of the catchup math instance.
	 * 
	 * @return
	 */
	public int  getContextCompletionPercent();
	

	/** Return list of custom tools needed for this context
	 * 
	 * @return
	 */
	public List<Component> getTools();
	
	
	/** Return the sub title used and placed in the header of west panel
	 * 
	 * @return
	 */
	public String getContextSubTitle();
	
	
	
	public String getStatusMessage();
	
}