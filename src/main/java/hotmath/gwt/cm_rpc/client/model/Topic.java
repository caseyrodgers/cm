package hotmath.gwt.cm_rpc.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Topic implements IsSerializable {
    String name;
    String file;
    String excerpt;

    public Topic() {}
    
    public Topic(String topic, String file, String excerpt) {
        this.name = topic;
        this.file = file;
        this.excerpt = excerpt;
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

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    @Override
    public String toString() {
        return "Topic [name=" + name + ", file=" + file + ", excerpt=" + excerpt + "]";
    }
    
    
    @Override
    public boolean equals(Object obj) {
        
        if(obj instanceof Topic) {
            return ((Topic)obj).getFile().equals(getFile());
        }
        else {
            return super.equals(obj);
        }
    }
}
