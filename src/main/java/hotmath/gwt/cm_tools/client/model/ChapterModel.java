package hotmath.gwt.cm_tools.client.model;

public class ChapterModel extends BaseModel {
	
	private static final long serialVersionUID = 8712800389384865595L;

	public ChapterModel (String number, String title) {
		set("number", number);
		set("title", title);
		set("chapter", number + " " + title);
	}
		
	public String getNumber() {
		return get("number");
	}
	
	public String getTitle() {
		return get("title");
	}
	
	public String getChapter() {
		return get("chapter");
    }
	
	public ChapterModel() {
	}
}
