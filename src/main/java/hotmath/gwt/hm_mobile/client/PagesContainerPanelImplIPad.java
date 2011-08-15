package hotmath.gwt.hm_mobile.client;


import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;

import com.allen_sauer.gwt.log.client.Log;

public class PagesContainerPanelImplIPad extends PagesContainerPanelImplIPhone implements PagesContainerPanel {

    public PagesContainerPanelImplIPad() {
    	Log.info("PagesContainerPanelImplIPad: startup");
    }

    @Override
    public void addPage(IPage p) {
    	super.addPage(p);
    	/** bypass animation with IPad.  It does not 
    	 *  seem to support ontransistion css animation
    	 *  callback.
    	 *  
    	 */
    	ViewSettings.AnimationRunning = false;
    }
    
    @Override
    public void removePage(IPage removedPage) {
    	super.removePage(removedPage);
    	
    	/** bypass animation 
    	 * 
    	 */
    	ViewSettings.AnimationRunning = false;
    }

}
