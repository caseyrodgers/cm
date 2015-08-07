package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.io.Serializable;
import java.util.List;

public class SearchSuggestion implements Response {
    
    private String word;

    CmList<String> options = new CmArrayList<String>();
    
    public CmList<String> getOptions() {
        return options;
    }

    public void setOptions(CmList<String> options) {
        this.options = options;
    }

    public SearchSuggestion() { }
    
    public SearchSuggestion(String word, List<String> options) {
        this.word = word;
        if(options != null) {
            this.options.addAll(options);
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getSuggestion() {
        return options.size() > 0?options.get(0):null;
    }


}
