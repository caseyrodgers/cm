package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;



public class GetTopicPrescriptionAction implements Action<PrescriptionSessionResponse> {

    String topicFile;
    int uid;

    public GetTopicPrescriptionAction() {}

    /** Return an anonymous prescription  for this lesson
     *
     * @param runId
     * @param sessionNumber
     * @param updateActiveInfo
     */
    public GetTopicPrescriptionAction(String topicFile) {
        this.topicFile = topicFile;
    }
    
    public GetTopicPrescriptionAction(String topicFile, int forUid) {
        this(topicFile);
        this.uid = forUid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
