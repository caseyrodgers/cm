package hotmath.gwt.hm_mobile.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/** The main body selection place
 * 
 * @author casey
 *
 */
public class CategoryListPlace extends Place {
	String token;
	
	public CategoryListPlace(String token) {
		this.token = token;
	}
	

	public String getToken() {
		return token;
	}
	
	public static class Tokenizer implements PlaceTokenizer<CategoryListPlace> {
		@Override
		public String getToken(CategoryListPlace place) {
			return place.getToken();
		}

		@Override
		public CategoryListPlace getPlace(String token) {
			return new CategoryListPlace(token);
		}
	}
}
