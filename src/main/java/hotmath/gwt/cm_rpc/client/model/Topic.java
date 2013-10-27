package hotmath.gwt.cm_rpc.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Topic implements IsSerializable {
    String name;
    String file;

    public Topic() {}
    
    public Topic(String topic, String file) {
        this.name = topic;
        this.file = file;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "Topic [name=" + name + ", file=" + file + "]";
    }
}