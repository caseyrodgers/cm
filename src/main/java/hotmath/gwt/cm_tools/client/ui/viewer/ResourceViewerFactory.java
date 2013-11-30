package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanelImplDefault;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.ResourceViewerImplWebLink;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

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

        CmResourceType type = item.getType();

        CmLogger.debug("ResourceViewerFactory: creating new resource viewer: " + type);

        CmResourcePanel rp = null;

        switch (type) {
        case PRACTICE:
            /**
             * RPs might be either a tutor (RPP) or a flash widget (RPA)
             * 
             */
            String file = item.getFile();
            if (file.indexOf(".swf") > -1) {
                /**
                 * is widget TODO: correct abstraction
                 */
                rp = new ResourceViewerImplRppFlashCard();
            } else {
                rp = new ResourceViewerImplTutor2();
            }
            break;

        case VIDEO:
            rp = new ResourceViewerImplVideo();
            break;

        case ACTIVITY:
        case ACTIVITY_STANDARD:
            rp = new ResourceViewerImplActivity();
            break;

        case WORKBOOK:
            rp = new ResourceViewerImplWorkbook();
            break;

        case REVIEW:
            rp = new ResourceViewerImplReview();
            break;

        case TESTSET:
            rp = new ResourceViewerImplQuiz();
            break;

        case RESULTS:
            rp = new ResourceViewerImplResults();
            break;

        case CMEXTRA:
            rp = new ResourceViewerImplTutor2();
            break;

        case FLASHCARD:
            rp = new ResourceViewerImplFlashCard();
            break;

        case FLASHCARD_SPANISH:
            rp = new ResourceViewerImplFlashCard();
            break;
            
        case WEBLINK:
        case WEBLINK_EXTERNAL:   
            rp = new ResourceViewerImplWebLink();
            break;
            
        default:
            rp = new CmResourcePanelImplDefault();
            break;
        }
        rp.setResourceItem(item);
        return rp;
    }
}
