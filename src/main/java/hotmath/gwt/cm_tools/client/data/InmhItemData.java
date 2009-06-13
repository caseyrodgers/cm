package hotmath.gwt.cm_tools.client.data;

public class InmhItemData {
	
	String type;
	String title;
	String file;
	boolean viewed;
	
	public InmhItemData() {
	    // 
	    int i=1;
	}
	public boolean isViewed() {
        return viewed;
    }
    public void setViewed(boolean viewed) {
        this.viewed = viewed;
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
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	
	public String toString() {
		return type + ", " + file + ", " +  title;
	}
}
