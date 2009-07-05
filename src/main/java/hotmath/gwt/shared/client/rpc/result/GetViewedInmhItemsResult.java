package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.List;

public class GetViewedInmhItemsResult implements Response {
    
    List<RpcData> rpcData;
    
    public GetViewedInmhItemsResult(List<RpcData> data) {
        this.rpcData = data;
    }

    public List<RpcData> getRpcData() {
        return rpcData;
    }

    public void setRpcData(List<RpcData> rpcData) {
        this.rpcData = rpcData;
    }

}
