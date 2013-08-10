package hotmath.gwt.shared.client.model;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.FolderDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CCSSGradeLevel extends FolderDto implements IsSerializable {

	private static final long serialVersionUID = -4947369230435350838L;

	protected String name;
	protected CCSSData parent;
	protected CmList<CCSSDomain> domains;

	public CCSSGradeLevel() {}

	public CCSSGradeLevel(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public CmList<CCSSDomain> getDomains() {
		return domains;
	}

	public void setDomains(CmList<CCSSDomain>  domains) {
		this.domains = domains;
	}

	@Override
	public int getLevel() {
		return CCSSData.GRADE;
	}

	public String getLabel() {
		return name;
	}

	public CCSSData getParent() {
		return parent;
	}

	public void setParent(CCSSData parent) {
		this.parent = parent;
	}

	public boolean hasChildren() {
		return domains.size() > 0;
	}

}
