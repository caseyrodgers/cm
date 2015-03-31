package hotmath.gwt.cm_rpc.client.rpc.cm2;


import hotmath.gwt.cm_rpc.client.rpc.CreateTestRunResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class QuizCm2CheckedResult implements Response {
    private CreateTestRunResponse testRunResults;

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
}
