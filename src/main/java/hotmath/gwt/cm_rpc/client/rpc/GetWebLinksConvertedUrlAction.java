package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_core.client.model.WebLinkConvertedUrlModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

/** Take a WebLink URL and do any processing/conversion  needed
 * 
 * @author casey
 *
 */
public class GetWebLinksConvertedUrlAction implements Action<WebLinkConvertedUrlModel> {

    String url;

    public GetWebLinksConvertedUrlAction() {}

    public GetWebLinksConvertedUrlAction(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
