package hotmath.gwt.shared.client.util;

import com.allen_sauer.gwt.log.client.Log;
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
	    Log.info("Error loading fragment", reason);
	    new SystemVersionUpdateChecker();
	}
}
