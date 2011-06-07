package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.ui.CmLogger;

import com.google.gwt.core.client.RunAsyncCallback;

/** Provide standard RunAsync behavior for failure
 * 
 * @author casey
 *
 */
public abstract class CmRunAsyncCallback implements RunAsyncCallback {

	@Override
	abstract public void onSuccess();

	@Override
	public void onFailure(Throwable reason) {
	    CmLogger.error("Error loading fragment", reason);
	    new SystemSyncChecker();
	}
}
