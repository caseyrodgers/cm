package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.CmWebResource;

public class GeneratePdfParallelProgramUsageReportAction implements Action<CmWebResource>{

	private static final long serialVersionUID = 6815765230412912376L;

	Integer adminId;
    Integer parallelProgId;

	public GeneratePdfParallelProgramUsageReportAction() {
	}

	public GeneratePdfParallelProgramUsageReportAction(Integer adminId, Integer parallelProgId) {
        this.adminId = adminId;
        this.parallelProgId = parallelProgId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getParallelProgId() {
		return parallelProgId;
	}

	public void setParallelProgId(Integer parallelProgId) {
		this.parallelProgId = parallelProgId;
	}

	@Override
    public String toString() {
        return "GeneratePdfParallelProgramUsageReportAction [adminId: " + adminId + ", parallelProgId: " + parallelProgId + "]";
    }
}
