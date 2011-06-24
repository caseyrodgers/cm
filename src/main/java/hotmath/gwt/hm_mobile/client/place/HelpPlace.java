package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class HelpPlace extends Place {
	String token;
	
	public HelpPlace(String token) {
		this.token = token;
	}
	

	public String getToken() {
		return token;
	}
	
	public static class Tokenizer implements PlaceTokenizer<HelpPlace> {
		@Override
		public String getToken(HelpPlace place) {
			return place.getToken();
		}

		@Override
		public HelpPlace getPlace(String token) {
			return new HelpPlace(token);
		}
	}
}
