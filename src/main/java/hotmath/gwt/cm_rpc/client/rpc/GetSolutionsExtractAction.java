package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SolutionExtractResults;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;

import java.util.List;

public class GetSolutionsExtractAction implements Action<SolutionExtractResults> {

    private CmList<SolutionSearchModel> res = new CmArrayList<SolutionSearchModel>();
    public GetSolutionsExtractAction() {}
    public GetSolutionsExtractAction(List<SolutionSearchModel> res) {
        this.res.addAll(res);
    }
    public List<SolutionSearchModel> getRes() {
        return res;
    }
    public void setRes(CmList<SolutionSearchModel> res) {
        this.res = res;
    }

}
