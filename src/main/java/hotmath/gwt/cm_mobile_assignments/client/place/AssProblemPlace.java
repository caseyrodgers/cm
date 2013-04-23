package hotmath.gwt.cm_mobile_assignments.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AssProblemPlace extends Place {
    
    private int assignKey;
    private String pid;
    
    public AssProblemPlace(int assignKey, String pid) {
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
    
    public static class Tokenizer implements PlaceTokenizer<AssProblemPlace> {
        @Override
        public String getToken(AssProblemPlace place) {
            return place.getToken();
        }

        @Override
        /** Parse token to identify a single problem in assignment
         * ASS_KEY | PID
         */
        public AssProblemPlace getPlace(String assProb) {
            String[] t = assProb.split("\\|");
            return new AssProblemPlace(Integer.parseInt(t[0]),t[1]);
        }
    }
}
