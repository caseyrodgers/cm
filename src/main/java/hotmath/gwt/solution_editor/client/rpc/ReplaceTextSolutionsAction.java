package hotmath.gwt.solution_editor.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;

import java.util.List;

public class ReplaceTextSolutionsAction implements Action<RpcData>{
    private List<SolutionSearchModel> pidsToReplace;
    private String searchFor;
    private String replaceWith;

    public ReplaceTextSolutionsAction() {}

    public ReplaceTextSolutionsAction(CmList<SolutionSearchModel> res, String searchFor, String replaceWith) {
       this.pidsToReplace = res;
       this.searchFor = searchFor;
       this.replaceWith = replaceWith;
    }

    public List<SolutionSearchModel> getPidsToReplace() {
        return pidsToReplace;
    }

    public void setPidsToReplace(List<SolutionSearchModel> pidsToReplace) {
        this.pidsToReplace = pidsToReplace;
    }

    public String getSearchFor() {
        return searchFor;
    }

    public void setSearchFor(String searchFor) {
        this.searchFor = searchFor;
    }

    public String getReplaceWith() {
        return replaceWith;
    }

    public void setReplaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
    }

    @Override
    public String toString() {
        return "ReplaceTextSolutionsAction [pidsToReplace=" + pidsToReplace + ", searchFor=" + searchFor
                + ", replaceWith=" + replaceWith + "]";
    }
    
}
