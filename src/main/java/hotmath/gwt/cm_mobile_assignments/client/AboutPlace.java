package hotmath.gwt.cm_mobile_assignments.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AboutPlace extends Place {
    
    String token;
    
    public AboutPlace() {
        this.token = "about";
    }
    

    public String getToken() {
        return token;
    }
    
    public static class Tokenizer implements PlaceTokenizer<AboutPlace> {
        @Override
        public String getToken(AboutPlace place) {
            return place.getToken();
        }

        @Override
        public AboutPlace getPlace(String token) {
            return new AboutPlace();
        }
    }

}
