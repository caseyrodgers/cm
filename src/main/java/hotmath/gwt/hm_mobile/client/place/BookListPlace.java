package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class BookListPlace extends Place {
	String token;
	
	public BookListPlace(String token) {
	}
	

	public String getToken() {
		return token;
	}
	
	public static class Tokenizer implements PlaceTokenizer<BookListPlace> {
		@Override
		public String getToken(BookListPlace place) {
			return place.getToken();
		}

		@Override
		public BookListPlace getPlace(String token) {
			return new BookListPlace(token);
		}
	}
}
