package hotmath.gwt.cm_mobile_shared.client.rpc;

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
}
