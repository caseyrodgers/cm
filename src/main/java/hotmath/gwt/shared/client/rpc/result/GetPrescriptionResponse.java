package hotmath.gwt.shared.client.rpc.result;

import com.google.gwt.user.client.rpc.IsSerializable;

import hotmath.gwt.shared.client.rpc.Response;

public class GetPrescriptionResponse implements Response, IsSerializable {
    public GetPrescriptionResponse() {
    }
    
    public String getResult() {
        return "THE RESULT";
    }
}
