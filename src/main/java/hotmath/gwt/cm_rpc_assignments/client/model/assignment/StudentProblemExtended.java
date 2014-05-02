package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import java.util.Date;

public class StudentProblemExtended extends StudentProblemDto {

	private static final long serialVersionUID = 6111707382068282541L;

	private Date createDate;

	public StudentProblemExtended() {
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
