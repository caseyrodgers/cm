package hotmath.gwt.shared.client.util;

import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

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
			Window.alert("Error loading GWT fragment: " + reason.getLocalizedMessage());
	}
}
