package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CCSSStandard extends FolderDto implements IsSerializable {

	private static final long serialVersionUID = 7310627471162408012L;

	protected String originalName;
	protected String name;
	protected String summary;
	protected String description;
	protected boolean hasChildren;
	protected CCSSDomain parent;

	protected CmList<CCSSLesson> lessons;

	public CCSSStandard() {}

	public CCSSStandard(String name, String originalName, String summary, String description) {
		this.name = name;
		this.originalName = originalName;
		this.summary = summary;
		this.description = description;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int getLevel() {
		return CCSSData.STANDARD;
	}

	public String getLabel() {
		return summary;
	}

	public CmList<CCSSLesson> getLessons() {
		return lessons;
	}

	public void setLessons(CmList<CCSSLesson> lessons) {
		this.lessons = lessons;
	}

	public void setParent(CCSSDomain parent) {
		this.parent = parent;;
	}

	public boolean hasChildren() {
		return lessons.size() > 0;
	}
}
