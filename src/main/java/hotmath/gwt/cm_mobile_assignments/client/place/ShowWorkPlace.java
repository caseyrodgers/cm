package hotmath.gwt.cm_mobile_assignments.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ShowWorkPlace extends Place {
    
    private int assignKey;
    private String pid;

    public ShowWorkPlace(int assignKey, String pid) {
        this.assignKey = assignKey;
        this.pid = pid;
    }

    public int getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(int assignKey) {
        this.assignKey = assignKey;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
    
    public String getToken() {
        return assignKey + "|" + pid;
    }
    
    public static class Tokenizer implements PlaceTokenizer<ShowWorkPlace> {
        @Override
        public String getToken(ShowWorkPlace place) {
            return place.getToken();
        }

        @Override
        /** Parse token to identify a single problem in assignment
         * ASS_KEY | PID
         */
        public ShowWorkPlace getPlace(String assProb) {
            String[] t = assProb.split("\\|");
            return new ShowWorkPlace(Integer.parseInt(t[0]),t[1]);
        }
    }    
}
