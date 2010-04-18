package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.result.CmVersionInfo;

public class GetCmVersionInfoAction implements Action<CmVersionInfo> {
    String baseUrl;
    
    public GetCmVersionInfoAction() {}
    
    public GetCmVersionInfoAction(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
