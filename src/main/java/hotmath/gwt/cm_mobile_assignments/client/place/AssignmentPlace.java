package hotmath.gwt.cm_mobile_assignments.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AssignmentPlace extends Place {

    private int assignKey;

    public AssignmentPlace(int assignKey) {
        this.assignKey = assignKey;
    }

    public int getAssignKey() {
        return assignKey;
    }
    
    public String getToken() {
        return Integer.toString(assignKey);
    }
    
    public static class Tokenizer implements PlaceTokenizer<AssignmentPlace> {
        @Override
        public String getToken(AssignmentPlace place) {
            return place.getToken();
        }

        @Override
        public AssignmentPlace getPlace(String assignKeyAsStr) {
            return new AssignmentPlace(Integer.parseInt(assignKeyAsStr));
        }
    }
    
}
