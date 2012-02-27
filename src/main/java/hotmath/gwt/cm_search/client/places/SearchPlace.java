package hotmath.gwt.cm_search.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class SearchPlace extends Place {
    
    String token;
    
    public SearchPlace(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }

    public static class Tokenizer implements PlaceTokenizer<SearchPlace> {
        @Override
        public String getToken(SearchPlace place) {
            return place.getToken();
        }

        @Override
        public SearchPlace getPlace(String token) {
            return new SearchPlace(token);
        }
    }
}
