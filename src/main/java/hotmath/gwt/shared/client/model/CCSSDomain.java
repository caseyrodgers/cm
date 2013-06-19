package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CCSSDomain extends FolderDto implements IsSerializable {

	private static final long serialVersionUID = 4714646244500440929L;

	protected String name;
	protected CCSSGradeLevel parent;
	protected CmList<CCSSStandard> standards;

    public CCSSDomain() {}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public CmList<CCSSStandard> getStandards() {
		return standards;
	}

	public void setStandards(CmList<CCSSStandard> standards) {
		this.standards = standards;
	}

	public int getLevel() {
		return CCSSData.DOMAIN;
	}

	public String getLabel() {
		return name;
	}

	public CCSSGradeLevel getParent() {
		return parent;
	}

	public void setParent(CCSSGradeLevel parent) {
		this.parent = parent;;
	}

	public boolean hasChildren() {
		return standards.size() > 0;
	}
}
