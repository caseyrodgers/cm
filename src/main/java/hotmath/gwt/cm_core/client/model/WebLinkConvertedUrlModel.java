package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/** Contains a WebLink converted URL
 * 
 * @author casey
 *
 */
public class WebLinkConvertedUrlModel implements Response{
    String convertedUrl;
    String originalUrl;
    
    public WebLinkConvertedUrlModel(){}
    
    public WebLinkConvertedUrlModel(String originalUrl, String convertedUrl) {
        this.originalUrl = originalUrl;
        this.convertedUrl = convertedUrl;
    }

    public String getConvertedUrl() {
        return convertedUrl;
    }

    public void setConvertedUrl(String convertedUrl) {
        this.convertedUrl = convertedUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    @Override
    public String toString() {
        return "WebLinkConvertedUrlModel [convertedUrl=" + convertedUrl + ", originalUrl=" + originalUrl + "]";
    }
    

}
