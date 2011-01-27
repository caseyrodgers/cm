package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;

public class SearchForSolutionsAction implements Action<CmList<SolutionSearchModel>>{
    
    String searchFor;
    String searchText;
    
    public SearchForSolutionsAction() {}
    
    public SearchForSolutionsAction(String searchFor, String searchText) {
        this.searchFor = searchFor;
        this.searchText = searchText;
    }

    public String getSearchFor() {
        return searchFor;
    }

    public void setSearchFor(String searchFor) {
        this.searchFor = searchFor;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
