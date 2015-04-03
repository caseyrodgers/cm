package hotmath.gwt.cm_core.client.model;

import java.util.List;

public class Cm2PrescriptionTopic {
    
    private String topic;
    private List<PrescriptionResource> resources;

    public Cm2PrescriptionTopic() {}
    
    public Cm2PrescriptionTopic(String topic, List<PrescriptionResource> resources) {
        this.topic = topic;
        this.resources = resources;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<PrescriptionResource> getResources() {
        return resources;
    }

    public void setResources(List<PrescriptionResource> resources) {
        this.resources = resources;
    }

}
