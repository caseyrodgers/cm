package hotmath.gwt.cm_search.client.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class TopicPlace extends Place {
    
    String token;
    
    public TopicPlace(String token) {
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }

    public static class Tokenizer implements PlaceTokenizer<TopicPlace> {
        @Override
        public String getToken(TopicPlace place) {
            return place.getToken();
        }

        @Override
        public TopicPlace getPlace(String token) {
            return new TopicPlace(token);
        }
    }
}
