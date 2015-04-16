package hotmath.gwt.cm_core.client.model;

public class ResourceItem {
    
    private String file;
    private String type;
    private String title;
    private boolean isViewed;

    public ResourceItem() {}
    
    public ResourceItem(String type, String file, String title, boolean isViewed) {
        this.type = type;
        this.file = file;
        this.title = title;
        this.isViewed = isViewed;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean isViewed) {
        this.isViewed = isViewed;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
