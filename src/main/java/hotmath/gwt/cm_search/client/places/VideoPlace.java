package hotmath.gwt.cm_search.client.places;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class VideoPlace extends Place {
    
    String token;
    InmhItemData videoResource;
    
    public VideoPlace(String token) {
        this.token = token;
    }
    
    public VideoPlace(InmhItemData resource) {
        this.videoResource = resource;
    }
    
    public InmhItemData getResource() {
        return videoResource;
    }
    
    public String getToken() {
        return token;
    }

    public static class Tokenizer implements PlaceTokenizer<VideoPlace> {
        @Override
        public String getToken(VideoPlace place) {
            return place.getToken();
        }

        @Override
        public VideoPlace getPlace(String token) {
            return new VideoPlace(token);
        }
    }
}
