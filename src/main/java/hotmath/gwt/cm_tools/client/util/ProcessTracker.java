package hotmath.gwt.cm_tools.client.util;

/**
 * interface to support tracking of a multi-step process, ie - several RPCs for populating a UI dialog
 * 
 * @author bob
 *
 */

public interface ProcessTracker {
	
	public void beginStep();
	
	public void completeStep();
	
	public void finish();

}
