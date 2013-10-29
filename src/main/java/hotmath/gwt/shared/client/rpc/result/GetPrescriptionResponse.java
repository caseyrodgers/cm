package hotmath.gwt.shared.client.rpc.result;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GetPrescriptionResponse implements Response, IsSerializable {
    public GetPrescriptionResponse() {
    }
    
    public String getResult() {
        return "THE RESULT";
    }
}
