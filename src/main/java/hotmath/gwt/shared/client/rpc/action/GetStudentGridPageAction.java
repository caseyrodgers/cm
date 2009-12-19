package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;
import hotmath.gwt.shared.client.rpc.Action;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class GetStudentGridPageAction implements Action<CmStudentPagingLoadResult<StudentModelExt>>{
    
    Integer adminId;
	PagingLoadConfig loadConfig;
	String groupFilter;
	boolean forceRefresh;
	
	public GetStudentGridPageAction() {}
	
	public GetStudentGridPageAction(Integer adminId,PagingLoadConfig loadConfig) {
	    this.adminId = adminId;
		this.loadConfig = loadConfig;
	}

	public PagingLoadConfig getLoadConfig() {
		return loadConfig;
	}

	public void setLoadConfig(PagingLoadConfig loadConfig) {
		this.loadConfig = loadConfig;
	}

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public boolean isForceRefresh() {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    public String getGroupFilter() {
        return groupFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }
}
