package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetViewedInmhItemsResult implements Response, IsSerializable {
    
    List<RpcData> rpcData;
    
    public GetViewedInmhItemsResult() {}
    
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
