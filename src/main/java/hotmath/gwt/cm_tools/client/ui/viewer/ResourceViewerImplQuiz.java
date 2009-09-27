package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.QuizPage;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplQuiz  extends ResourceViewerContainer implements ResourceViewer {
	
	public Widget getResourcePanel(InmhItemData resource) {
	    
	    this.item = resource;
	    
		return new QuizPage(new CmAsyncRequest() {
			public void requestComplete(String requestData) {
			}
			public void requestFailed(int code, String text) {
		}
		});
	}
}
