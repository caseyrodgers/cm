package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.CmArrayList;
import hotmath.gwt.shared.client.rpc.action.CmList;

public class LessonItemModel extends BaseModel implements Response{
	
	private static final long serialVersionUID = -9053678467730737870L;

	public static final String NAME_KEY = "name";
	public static final String FILE_KEY = "file";
	public static final String PRESCRIBED_KEY = "prescribed";
	
	private String name;
	private String file;
	private String prescribed;
	
	CmList<String> stateStandards = new CmArrayList<String>();
	
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
	

    public CmList<String> getStateStandards() {
        return stateStandards;
    }

    public void setStateStandards(CmList<String> stateStandards) {
        this.stateStandards = stateStandards;
    }
}
