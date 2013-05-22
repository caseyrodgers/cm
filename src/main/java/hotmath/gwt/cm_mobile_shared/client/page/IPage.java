package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

public interface IPage {
    
    public enum ApplicationType {
        /** is a page in a normal CM program */
        PROGRAM,
        
        /** is a page in assignments */
        ASSIGNMENT,
        
        /** Is not in a program/not logged in */
        NONE
    }

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

    ApplicationType getApplicationType();

}
