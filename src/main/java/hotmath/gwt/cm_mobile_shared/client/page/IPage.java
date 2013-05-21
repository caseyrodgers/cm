package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

public interface IPage {

	static public interface BackAction {
	    /** provide back processing, if return true
	     *  then normal processing will continue.
	     *  
	     * @return
	     */
	    boolean goBack(); 
	}
	
	String getViewTitle();

	String getBackButtonText();
	
	List<ControlAction> getControlFloaterActions();
	
	TokenParser getBackButtonLocation();
	
	BackAction getBackAction();

	/** Called once page is visible to user
	 * 
	 */
    void isNowActive();

}
