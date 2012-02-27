package hotmath.gwt.cm_search.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class TutorPlace extends Place {
    
    String token;
    
    public TutorPlace(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }

    public static class Tokenizer implements PlaceTokenizer<TutorPlace> {
        @Override
        public String getToken(TutorPlace place) {
            return place.getToken();
        }

        @Override
        public TutorPlace getPlace(String token) {
            return new TutorPlace(token);
        }
    }
}
