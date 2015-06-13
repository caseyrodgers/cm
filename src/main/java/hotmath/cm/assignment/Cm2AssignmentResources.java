package hotmath.cm.assignment;

import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cm2AssignmentResources {
    
    Map<String, List<PrescriptionSessionResponse>> resourceMap = new HashMap<String, List<PrescriptionSessionResponse>>();
    public Cm2AssignmentResources() {}
    
    public void setPidResources(String pid, List<PrescriptionSessionResponse> results) {
        resourceMap.put(pid, results);
    }

    public Map<String, List<PrescriptionSessionResponse>> getResourceMap() {
        return resourceMap;
    }

    public void setResourceMap(Map<String, List<PrescriptionSessionResponse>> resourceMap) {
        this.resourceMap = resourceMap;
    }
    
}
