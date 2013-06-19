package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CCSSData extends FolderDto implements Response {

	private static final long serialVersionUID = -5793823416934583310L;

	private String name;
	private CmList<CCSSGradeLevel> levels;

	public static final int ROOT     = 0;
	public static final int GRADE    = 1;
	public static final int DOMAIN   = 2;
 	public static final int STANDARD = 3;
 	public static final int LESSON   = 4;

    public CCSSData() { }

	public CCSSData(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CmList<CCSSGradeLevel> getLevels() {
		return levels;
	}

	public void setLevels(CmList<CCSSGradeLevel> levels) {
		this.levels = levels;
	}

	public int getLevel() {
		return ROOT;
	}

	public String getLabel() {
		return name;
	}

	public boolean hasChildren() {
		return levels.size() > 0;
	}
}
