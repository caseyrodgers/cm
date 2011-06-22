package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class HomePlace extends Place {
	String token;
	
	public HomePlace(String token) {
		this.token = token;
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
			return new HomePlace(token);
		}
	}
}
