package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.ui.QuizPage;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplQuiz  extends ResourceViewerContainer implements ResourceViewer {
	
	public Widget getResourcePanel(InmhItemData resource) {
		return new QuizPage(new CmAsyncRequest() {
			public void requestComplete(String requestData) {
			}
			public void requestFailed(int code, String text) {
		}
		});
	}
}
