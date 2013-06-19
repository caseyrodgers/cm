package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.BaseDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CCSSLesson extends FolderDto implements IsSerializable {

	private static final long serialVersionUID = 1014135884820005503L;

	protected String name;
    protected String file;
	protected CCSSStandard parent;
	CmList<BaseDto> problems;

	public CCSSLesson() {}

	public CCSSLesson(String name, String file) {
		this.name = name;
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

	public int getLevel() {
		return CCSSData.LESSON;
	}

	public String getLabel() {
		return name;
	}

	public CCSSStandard getParent() {
		return parent;
	}
	
	public void setParent(CCSSStandard parent) {
		this.parent = parent;;
	}

	public boolean hasChildren() {
		return problems != null && problems.size() > 0;
	}

	public CmList<BaseDto> getProblems() {
		return problems;
	}
	
	public void setProblems(CmList<BaseDto> problems) {
		this.problems = problems;
	}
}
