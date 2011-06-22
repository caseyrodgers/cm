package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class BookViewPlace extends Place {
	String token;
	
	public BookViewPlace(String token) {
	}
	

	public String getToken() {
		return token;
	}
	
	public static class Tokenizer implements PlaceTokenizer<BookViewPlace> {
		@Override
		public String getToken(BookViewPlace place) {
			return place.getToken();
		}

		@Override
		public BookViewPlace getPlace(String token) {
			return new BookViewPlace(token);
		}
	}
}
