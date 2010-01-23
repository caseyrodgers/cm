package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.CatchupMathTools;

import com.google.gwt.user.client.rpc.AsyncCallback;

abstract public class CmAsyncCallback<T> implements AsyncCallback<T>{

    /** Provide standard exception processing and display
     * 
     */
	@Override
	public void onFailure(Throwable caught) {
		caught.printStackTrace();
		CatchupMathTools.showAlert(caught.getLocalizedMessage());
	}

	@Override
	abstract public void onSuccess(T result);

}
