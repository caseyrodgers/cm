package hotmath.gwt.cm_tools.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class LessonItemModel extends BaseModelData {
	
	private static final long serialVersionUID = -9053678467730737870L;

	public static final String NAME_KEY = "name";
	public static final String FILE_KEY = "file";
	public static final String PRESCRIBED_KEY = "prescribed";
	
	private String name;
	private String file;
	private String prescribed;
	
	public String getPrescribed() {
		return prescribed;
	}

	public void setPrescribed(String prescribed) {
        this.prescribed = prescribed;
		set(PRESCRIBED_KEY, prescribed);
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		set(NAME_KEY, name);
	}
	
	public String getFile() {
		return file;
	}
	
	public void setFile(String file) {
		this.file= file;
		set(FILE_KEY, name);
	}
}
