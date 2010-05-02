package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;

/**
 * Create the appropriate resource viewer
 * 
 * Follows GwtRunAsync pattern
 *   
 * @author Casey
 * 
 */
public class ResourceViewerFactory {

	private static ResourceViewerFactory instance;

	public interface ResourceViewerFactory_Client {
		void onSuccess(ResourceViewerFactory instance);

		void onUnavailable();
	}

	/**
	 * Access the module's instance. The callback runs asynchronously, once the
	 * necessary code has down loaded.
	 */
	public static void createAsync(final ResourceViewerFactory_Client client) {
		GWT.runAsync(new CmRunAsyncCallback() {
			public void onSuccess() {
				if (instance == null) {
					instance = new ResourceViewerFactory();
				}
				client.onSuccess(instance);
			}
		});

	}

	/**
	 * Create the appropriate resource viewer type
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public CmResourcePanel create(InmhItemData item) throws Exception {

		String type = item.getType();

		Log.debug("ResourceViewerFactory: creating new resource viewer: "
				+ type);

		CmResourcePanel rp = null;
		if (type.equals("practice")) {
			rp = new ResourceViewerImplTutor();
		} else if (type.equals("video")) {
			rp = new ResourceViewerImplVideo();
		} else if (type.startsWith("activity")) {
			rp = new ResourceViewerImplActivity();
		} else if (type.equals("workbook")) {
			rp = new ResourceViewerImplWorkbook();
		} else if (type.equals("review")) {
			rp = new ResourceViewerImplReview();
		} else if (type.equals("testset")) {
			rp = new ResourceViewerImplQuiz();
		} else if (type.equals("results")) {
			rp = new ResourceViewerImplResults();
		} else if (type.equals("cmextra")) {
			rp = new ResourceViewerImplTutor();
		} else if (type.equals("flashcard")) {
			rp = new ResourceViewerImplFlashCard();
        } else if (type.equals("flashcard_spanish")) {
            rp = new ResourceViewerImplFlashCard();
			
		} else
			rp = new CmResourcePanelImplDefault();

		rp.setResourceItem(item);

		return rp;
	}
}
