package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;



public class GetTopicPrescriptionAction implements Action<PrescriptionSessionResponse> {

    String topicFile;

    public GetTopicPrescriptionAction() {}

    /** Return the Prescription data for this runid and session
     *
     * @param runId
     * @param sessionNumber
     * @param updateActiveInfo
     */
    public GetTopicPrescriptionAction(String topicFile) {
        this.topicFile = topicFile;
    }

    public String getTopicFile() {
        return topicFile;
    }

    public void setTopicFile(String topicFile) {
        this.topicFile = topicFile;
    }

    @Override
    public String toString() {
        return "GetTopicPrescriptionAction [topicFile=" + topicFile + "]";
    }
}
