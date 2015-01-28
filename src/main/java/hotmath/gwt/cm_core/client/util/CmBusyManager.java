package hotmath.gwt.cm_core.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;



/** Provides central control over the 'isBusy'
 *  status of the app.
 *  
 *   A stack of BusyStates are keep and restored in order.
 *   
 *   There are two types of busy indicators ... the 'mask' and the 
 *   'isBusy'.   The 'mask' put up a modal mask over the interface to 
 *   block user input.  The 'isBusy' simply puts the Busy indicator up.
 *   
 *   
 *   
 * @author casey
 *
 */
public class CmBusyManager {
	static List<BusyState> __busyStates = new ArrayList<BusyState>();

	
	public static interface BusyHandler {
        void hideMask();

        void showMask(BusyState state);
	}
	
	static BusyHandler __busyHandler;
	/** Set the main viewport used for isBusy masking
	 * 
	 * @param viewPort
	 */
	static public void setBusyHandler(BusyHandler handler) {
	    __busyHandler = handler;
	}
	
	static public BusyHandler getBusyHandler() {
	    if(__busyHandler == null) {
	        Window.alert("Busy handler not set");
	    }
	    return __busyHandler;
	}

	/**
     * Display or hide the modal busy dialog
     * 
     * Provides stack like functionality such that 
     * the 'busy' icon is only removed after the last
     * busy start.
     * 
     * If showMask is true, then cover entry GUI with mask while 
     * isBusy is true.
     * 
     * 
     * @param trueFalse
     * @param showMask
     */
    static public void setBusy(boolean trueFalse, boolean showMask) {
        if(trueFalse) {
        	// push it
            BusyState bs = new BusyState(showMask);
        	__busyStates.add(bs);
            showBusy(bs);
        }
        else {
            if(__busyStates.size()>0) {
            	// pop it
                BusyState bs = __busyStates.get(__busyStates.size()-1);
                __busyStates.remove(__busyStates.size()-1);
                
                hideBusy(bs);
            }
        }
    }
    
    /** Push a new busy state.
     * 
     *   Will display the mask and the busy indicator
     * 
     * @param trueFalse True to start new busystate, false to complete a corresponding one
     */
    static public void setBusy(boolean trueFalse) {
    	setBusy(trueFalse,true);
    }
    
    
    static private void showBusy(BusyState state) {
        __busyHandler.showMask(state);
    	showLoading(true);
    }
    
   static public void showLoading(boolean yesNo) {
       RootPanel.get("loading").setVisible(yesNo);       
   }
    
    /** Make the busy window disappear, 
     * no matter the state */
    static private void hideBusy(BusyState bs) {
    	//System.out.println("hideBusy " + (__busyStates.size()+1) + ": " + bs.useMask);
    	if(bs.useMask) {
    		/** only unmask, if there are no deeper masks
    		 * applied.  We do this to reduce flickering.
    		 */
    		boolean hasDeeperMask=false;
    		for(int i=0,t=__busyStates.size();i<t;i++) {
    			if(__busyStates.get(i) != bs)
    				if(__busyStates.get(i).useMask) {
    					hasDeeperMask = true;
    					break;
    				}
    		}
    		if(!hasDeeperMask) {
    		    getBusyHandler().hideMask();
    		}
    	}
    	
    	if(__busyStates.size() == 0) {
    		// System.out.println("Removing busy indicator");
    	    showLoading(false);
    	}
    }
    
    /** Force reset of the isBusy stack and
     *  making sure the Loading dialog is removed.
     *  
     */
    static public void resetBusy() {
        __busyStates.clear();
        hideBusy(new BusyState(true));
    }
    
    public static class BusyState {
        boolean useMask=true;
        public BusyState(boolean useMask) {
            this.useMask = useMask;
        }
    }
	
}


