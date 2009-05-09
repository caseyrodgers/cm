package hotmath.gwt.cm.client.ui.viewer;

import hotmath.gwt.cm.client.data.InmhItemData;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplReview extends ResourceViewerContainer implements ResourceViewer {
	public Widget getResourcePanel(final InmhItemData resource) {

		String file = "/hotmath_help/" + resource.getFile();
			 
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getHmContent(file,  "/hotmath_help/topics", new AsyncCallback() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			public void onSuccess(Object result) {
				String html = (String)result;
				
				addResource(new HTML(html),resource.getTitle());
			}
		});
		return this;
	}

}
