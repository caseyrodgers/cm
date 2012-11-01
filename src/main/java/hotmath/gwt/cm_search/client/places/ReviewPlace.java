package hotmath.gwt.cm_search.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class ReviewPlace extends Place {
    
    String token;
    
    public ReviewPlace(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }

    public static class Tokenizer implements PlaceTokenizer<ReviewPlace> {
        @Override
        public String getToken(ReviewPlace place) {
            return place.getToken();
        }

        @Override
        public ReviewPlace getPlace(String token) {
            return new ReviewPlace(token);
        }
    }
}
