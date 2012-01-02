package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class GetStudentGridPageAction implements Action<CmStudentPagingLoadResult<StudentModelExt>> {

	private static final long serialVersionUID = -3117922126418717986L;

	Integer adminId;
	PagingLoadConfig loadConfig;
	String groupFilter;
	boolean forceRefresh;
	String quickSearch;
	String dateRange;
	Map<FilterType,String> filterMap;
		
	public GetStudentGridPageAction() {
	}
	
	public GetStudentGridPageAction(Integer adminId, PagingLoadConfig loadConfig) {
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
    
    public String getDateRange() {
		return dateRange;
	}

	public void setDateRange(String dateRange) {
		this.dateRange = dateRange;
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
                + quickSearch + ", dateRange=" + dateRange + "]";
        
        return s;
    }
    public enum FilterType {GROUP, QUICKTEXT, DATE_RANGE}
}
