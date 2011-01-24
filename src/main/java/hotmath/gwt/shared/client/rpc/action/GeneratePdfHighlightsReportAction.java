package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.CmWebResource;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction.FilterType;

import java.util.Map;

public class GeneratePdfHighlightsReportAction implements Action<CmWebResource>{

	private static final long serialVersionUID = -4533387142154640743L;
	
	Integer adminId;
    GetStudentGridPageAction pageAction;
	Map<FilterType,String> filterMap;
	CmList<HighlightReportData> models;
	String reportName;
	CmList<String> columnLabels;
    
	public GeneratePdfHighlightsReportAction() {
	}
    
    public GeneratePdfHighlightsReportAction(Integer adminId, String reportName, GetStudentGridPageAction pageAction) {
        this.pageAction = pageAction;
        this.reportName = reportName;
        
        this.adminId = adminId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public GetStudentGridPageAction getPageAction() {
        return pageAction;
    }

    public void setPageAction(GetStudentGridPageAction pageAction) {
        this.pageAction = pageAction;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Map<FilterType,String> getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(Map<FilterType,String> filterMap) {
		this.filterMap = filterMap;
	}

    public CmList<HighlightReportData> getModels() {
        return models;
    }

    public void setModels(CmList<HighlightReportData> models) {
        this.models = models;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public CmList<String> getColumnLabels() {
		return columnLabels;
	}

	public void setColumnLabels(CmList<String> columnLabels) {
		this.columnLabels = columnLabels;
	}

	@Override
    public String toString() {
        return "GeneratePdfHighlightsReportAction [adminId=" + adminId + ", pageAction=" + pageAction + ", filterMap="
                + filterMap + ", models=" + models + ", reportName=" + reportName + ", columnLabels=" + columnLabels + "]";
    }
}
