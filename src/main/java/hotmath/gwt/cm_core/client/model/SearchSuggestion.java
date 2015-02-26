package hotmath.gwt.cm_core.client.model;

import java.io.Serializable;

public class SearchSuggestion implements Serializable {
    
    
    private String suggestion;

    public SearchSuggestion() { }
    
    public SearchSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
    
    
    @Override
    public String toString() {
        return suggestion;
    }

}
