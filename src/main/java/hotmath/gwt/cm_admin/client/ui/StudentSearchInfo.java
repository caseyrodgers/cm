package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.DateRangePickerDialog.FilterOptions;

/** Central place for holding info about current search options
 * 
 * @author casey
 *
 */
public class StudentSearchInfo {
    
    FilterOptions filterOptions;
    String quickSearch;
    int groupIdFilter;
    
    public FilterOptions getFilterOptions() {
        return filterOptions;
    }
    public void setFilterOptions(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
    }
    public String getQuickSearch() {
        return quickSearch;
    }
    public void setQuickSearch(String quickSearch) {
        this.quickSearch = quickSearch;
    }
    public int getGroupIdFilter() {
        return groupIdFilter;
    }
    public void setGroupIdFilter(int groupIdFilter) {
        this.groupIdFilter = groupIdFilter;
    }
}
