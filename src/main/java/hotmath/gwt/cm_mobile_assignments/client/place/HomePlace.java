package hotmath.gwt.cm_mobile_assignments.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class HomePlace extends Place {
    
    String token;
    
    public HomePlace() {
        this.token = "home";
    }
    

    public String getToken() {
        return token;
    }
    
    public static class Tokenizer implements PlaceTokenizer<HomePlace> {
        @Override
        public String getToken(HomePlace place) {
            return place.getToken();
        }

        @Override
        public HomePlace getPlace(String token) {
            return new HomePlace();
        }
    }

}
