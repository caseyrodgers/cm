package hotmath.gwt.shared.client.util;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

abstract public class CmAsyncCallback<T> implements AsyncCallback<T>{

	@Override
	public void onFailure(Throwable caught) {
		caught.printStackTrace();
		Window.alert(caught.getLocalizedMessage());
	}

	@Override
	abstract public void onSuccess(T result);

}
