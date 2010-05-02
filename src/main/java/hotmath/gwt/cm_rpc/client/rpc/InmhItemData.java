package hotmath.gwt.cm_rpc.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InmhItemData implements IsSerializable{
	
	String type;
	String title;
	String file;
	boolean viewed;
	
	public InmhItemData() {
	}
	
	public InmhItemData(String type, String file, String title) {
	    this.type = type;
	    this.file = file;
	    this.title = title;
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
