package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class SpellCheckSolutionsResults implements Response {

    private CmList<SpellCheckResults> results = new CmArrayList<SpellCheckResults>();
    
    public SpellCheckSolutionsResults() {}
    
    public SpellCheckSolutionsResults(List<SpellCheckResults> results) {
        this.results.addAll(results);
    }
    public List<SpellCheckResults> getResults() {
        return this.results;
    }

}
