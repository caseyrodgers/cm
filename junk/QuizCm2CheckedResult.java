package hotmath.gwt.cm_rpc.client.rpc.cm2;


import java.util.List;

import hotmath.gwt.cm_core.client.model.Cm2PrescriptionTopic;
import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class QuizCm2CheckedResult implements Response {
    private CreateTestRunResponse testRunResults;
    private List<Cm2PrescriptionTopic> prescriptionTopics;

    public QuizCm2CheckedResult() {}

    public QuizCm2CheckedResult(CreateTestRunResponse testRunResults) {
        this.testRunResults = testRunResults; 
    }

    public CreateTestRunResponse getTestRunResults() {
        return testRunResults;
    }

    public void setTestRunResults(CreateTestRunResponse testRunResults) {
        this.testRunResults = testRunResults;
    }

    public void setPrescriptionTopics(List<Cm2PrescriptionTopic> topics) {
        this.prescriptionTopics = topics;
        
    }

    public List<Cm2PrescriptionTopic> getPrescriptionTopics() {
        return prescriptionTopics;
    }
}
