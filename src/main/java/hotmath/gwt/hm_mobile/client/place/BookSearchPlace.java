package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class BookSearchPlace extends Place {
	String token;
	
	public BookSearchPlace(String token) {
	}
	

	public String getToken() {
		return token;
	}
	
	public static class Tokenizer implements PlaceTokenizer<BookSearchPlace> {
		@Override
		public String getToken(BookSearchPlace place) {
			return place.getToken();
		}

		@Override
		public BookSearchPlace getPlace(String token) {
			return new BookSearchPlace(token);
		}
	}
}
