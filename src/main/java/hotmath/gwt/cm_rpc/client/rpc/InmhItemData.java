package hotmath.gwt.cm_rpc.client.rpc;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.IsSerializable;

public class InmhItemData implements IsSerializable{
	
    CmResourceType type;
	String title;
	String file;
	String widgetJsonArgs;
    boolean viewed;
    
    
    public static enum CmResourceType {
        VIDEO, ACTIVITY,WORKBOOK,REVIEW,TESTSET,RESULTS,CMEXTRA,FLASHCARD,FLASHCARD_SPANISH,WEBLINK,WEBLINK_EXTERNAL, PRACTICE, ACTIVITY_STANDARD, ACTIVITY_JS;
        

        /** Map a type string into a CmResourceType
         * 
         * @param typeStr
         * @return
         */
        public static CmResourceType mapResourceType(String typeStr) {
            try {
                return CmResourceType.valueOf(typeStr.toUpperCase());
            }
            catch(Exception e) {
                Log.error("Could not map resource type: " + typeStr);
            }
            return null;
        }

        /** Resource unique lower case name */
        public String label() {
            return name().toLowerCase();
        }   

    }

    public InmhItemData() {
    }
    
	public InmhItemData(CmResourceType type, String file, String title) {
	    this(type,file,title,null);
	}
    public InmhItemData(CmResourceType type, String file, String title,String widgetJsonArgs) {
        this.type = type;
        this.file = file;
        this.title = title;
        this.widgetJsonArgs = widgetJsonArgs;
    }
    
    
	public String getWidgetJsonArgs() {
        return widgetJsonArgs;
    }

    public void setWidgetJsonArgs(String widgetJsonArgs) {
        this.widgetJsonArgs = widgetJsonArgs;
    }

	public boolean isViewed() {
        return viewed;
    }
    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
    public CmResourceType getType() {
		return type;
	}
	public void setType(CmResourceType type) {
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
