package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class MobilePlace extends Place {
	String token;

	public MobilePlace(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public static class Tokenizer implements PlaceTokenizer<MobilePlace> {
		@Override
		public String getToken(MobilePlace place) {
			return place.getToken();
		}

		@Override
		public MobilePlace getPlace(String token) {
			return new MobilePlace(token);
		}
	}
}
