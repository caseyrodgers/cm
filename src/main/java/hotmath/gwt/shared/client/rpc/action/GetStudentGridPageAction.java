package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

import java.util.HashMap;
import java.util.Map;

public class GetStudentGridPageAction implements Action<CmStudentPagingLoadResult<StudentModelExt>>{

    Integer adminId;
	PagingLoadConfig loadConfig;
	String groupFilter;
	boolean forceRefresh;
	String quickSearch;
	Map<FilterType,String> filterMap;
	
	// int newField;
	
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

    public String getQuickSearch() {
        return quickSearch;
    }

    public void setQuickSearch(String quickSearch) {
        this.quickSearch = quickSearch;
    }
    
    public void addFilter(FilterType key, String value) {
    	if (filterMap == null) filterMap = new HashMap<FilterType,String>();
    	filterMap.put(key, value);
    }
    
    public Map<FilterType,String> getFilterMap() {
    	return this.filterMap;
    }
    
    @Override
    public String toString() {
        String loadConfigStr=loadConfig==null?"NULL":" offset=" + loadConfig.getOffset();
        String s = "GetStudentGridPageAction [adminId=" + adminId + ", filterMap=" + filterMap + ", forceRefresh="
                + forceRefresh + ", groupFilter=" + groupFilter + "loadConfig=" + loadConfigStr + ", quickSearch="
                + quickSearch + "]";
        
        return s;
    }
    public enum FilterType {GROUP, QUICKTEXT};
}
