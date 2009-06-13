package hotmath.gwt.cm_tools.client.ui;


import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.util.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the different context switches for Catchup Math
 * 
 * @author Casey
 * 
 */
public class ContextController {
	
	
	static private ContextController __instance;
	static public ContextController getInstance() {
		if(__instance == null)
			__instance = new ContextController();
		return __instance;
	}
	
	CmContext theContext;
	public CmContext getTheContext() {
        return theContext;
    }

    public void setTheContext(CmContext theContext) {
        this.theContext = theContext;
    }

    List<ContextChangeListener> listeners = new ArrayList<ContextChangeListener>();
	
	/** Add a listener that will be notified when the context changes.
	 * 
	 *  The context that is current active will be passed as param.
	 *  
	 *  (does not allow duplicates)
	 *  
	 * @param listener
	 */
	public void addContextChangeListener(ContextChangeListener listener) {
	    if(!listeners.contains(listener))
	        listeners.add(listener);
	}
	
	/** Inform any listeners that a change has occurred
	 * 
	 */
	protected void fireContextChanged() {
	    for(ContextChangeListener listener: listeners) {
	        listener.contextChanged(theContext);
	    }
	}
	
	/** Remove name context if it exists */
	public void removeContextChangeListener(ContextChangeListener listener) {
	    if(listeners.contains(listener))
	        listeners.remove(listener);
	}
	
	/**
	 * Push a new context onto stack. This will call the resetContext of the
	 * context.
	 * 
	 * @param context
	 */
	 public void setCurrentContext(CmContext context) {
		 theContext = context;
        // Set the proper title and label info
        HeaderPanel.__instance.setHeaderTitle(context.getContextTitle());
        CmMainPanel.__lastInstance._westPanel.setHeading(context.getContextSubTitle());
  	    
        // HeaderPanel.__instance._thermometer.setPerecent(context.getContextCompletionPercent());
		fireContextChanged();
	}
	 
	 public String toString() {
	     String msg = (UserInfo.getInstance() != null)?UserInfo.getInstance().toString():"";
	     return msg;
	 }


	/**
	 * Move the system to the next logical state
	 * 
	 */
	 public void doNext() {
		theContext.doNext();
    }

	 public void doPrevious() {
		theContext.doPrevious();
	}
	
	 public void showHelp() {
		CatchupMathTools.showAlert(theContext.getContextHelp());
	}
}
