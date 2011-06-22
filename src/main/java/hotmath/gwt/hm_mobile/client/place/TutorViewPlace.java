package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class TutorViewPlace extends Place {
	String token;
	
	public TutorViewPlace(String token) {
	    this.token = token;
	}

	public String getToken() {
		return token;
	}
	
	public static class Tokenizer implements PlaceTokenizer<TutorViewPlace> {
		@Override
		public String getToken(TutorViewPlace place) {
			return place.getToken();
		}

		@Override
		public TutorViewPlace getPlace(String token) {
			return new TutorViewPlace(token);
		}
	}
}
