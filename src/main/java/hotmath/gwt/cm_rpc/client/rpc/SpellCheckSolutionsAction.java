package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.SpellCheckSolutionsResults;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;

import java.util.List;

public class SpellCheckSolutionsAction implements Action<SpellCheckSolutionsResults> {
    private CmList<SolutionSearchModel> res = new CmArrayList<SolutionSearchModel>();

    public SpellCheckSolutionsAction() {}

    public SpellCheckSolutionsAction(List<SolutionSearchModel> res) {
        this.res.addAll(res);
    }

    public CmList<SolutionSearchModel> getRes() {
        return res;
    }

    public void setRes(CmList<SolutionSearchModel> res) {
        this.res = res;
    }
}
