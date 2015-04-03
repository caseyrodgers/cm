package hotmath.gwt.cm_core.client.model;

public class TopicResource {
    
    private String type;
    private String file;

    public TopicResource() {}
    
    public TopicResource(String type, String file) {
        this.type = type;
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

}
