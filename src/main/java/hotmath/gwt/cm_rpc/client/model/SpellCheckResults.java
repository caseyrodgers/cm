package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_core.client.model.SearchSuggestion;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class SpellCheckResults implements Response {
    
    CmList<SearchSuggestion> cmList = new CmArrayList<SearchSuggestion>();
    private String message;
    public SpellCheckResults() {}

    public SpellCheckResults(List<SearchSuggestion> suggestList) {
        this.cmList.addAll(suggestList);
    }

    public SpellCheckResults(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CmList<SearchSuggestion> getCmList() {
        return cmList;
    }

    public void setCmList(CmList<SearchSuggestion> cmList) {
        this.cmList = cmList;
    }

}
