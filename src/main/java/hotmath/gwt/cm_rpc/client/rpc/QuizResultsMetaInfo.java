package hotmath.gwt.cm_rpc.client.rpc;


import com.google.gwt.user.client.rpc.IsSerializable;


/** Represents the returned values from a request for a 
 * quiz results.  Either old school HTML, or PDF
 * 
 * @author casey
 *
 */
public class QuizResultsMetaInfo implements Response, IsSerializable {
    
    public QuizResultsMetaInfo(){}

    RpcData rpcData;

    public RpcData getRpcData() {
        return rpcData;
    }

    public void setRpcData(RpcData rpcData) {
        this.rpcData = rpcData;
    }

    @Override
    public String toString() {
        return "QuizResultsMetaInfo [rpcData=" + rpcData + "]";
    }
    
    
}
