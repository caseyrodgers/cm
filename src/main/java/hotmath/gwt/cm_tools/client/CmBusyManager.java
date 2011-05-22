package hotmath.gwt.cm_tools.client;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.Viewport;
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

	
	static Viewport __viewPort;
	
	/** Set the main viewport used for isBusy masking
	 * 
	 * @param viewPort
	 */
	static public void setViewPort(Viewport viewPort) {
		__viewPort = viewPort;
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
    	//System.out.println("showBusy " + __busyStates.size() + ": " + state.useMask);
    	if(state.useMask) {
    		__viewPort.mask();
    	}
    	RootPanel.get("loading").setVisible(true);
    }
    
    
    /** Make the busy window disappear, no matter the state */
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
    			//System.out.println("Removing mask");
    	        __viewPort.unmask();
    		}
    	}
    	
    	if(__busyStates.size() == 0) {
    		// System.out.println("Removing busy indicator");
    	    RootPanel.get("loading").setVisible(false);
    	}
    	
    	__viewPort.unmask();
    }
    
    /** Force reset of the isBusy stack and
     *  making sure the Loading dialog is removed.
     *  
     */
    static public void resetBusy() {
        __busyStates.clear();
        hideBusy(new BusyState(true));
    }    
	
}


class BusyState {
	boolean useMask=true;
	public BusyState(boolean useMask) {
		this.useMask = useMask;
	}
}