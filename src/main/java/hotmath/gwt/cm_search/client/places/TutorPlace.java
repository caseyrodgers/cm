package hotmath.gwt.cm_search.client.places;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class TutorPlace extends Place {
    
    String token;
    private InmhItemData resourceItem;
    
    public TutorPlace(String token) {
        this.token = token;
    }
    
    public TutorPlace(InmhItemData resourceItem) {
        this(resourceItem.getFile());
        this.resourceItem = resourceItem;
    }

    public String getToken() {
        return token;
    }
    
    public InmhItemData getItem() {
        return resourceItem;
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
