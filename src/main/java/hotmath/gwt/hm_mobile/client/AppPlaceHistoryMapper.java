package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.hm_mobile.client.place.BookListPlace;
import hotmath.gwt.hm_mobile.client.place.CategoryListPlace;
import hotmath.gwt.hm_mobile.client.place.HomePlace;
import hotmath.gwt.hm_mobile.client.place.MobilePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ HomePlace.Tokenizer.class, BookListPlace.Tokenizer.class, CategoryListPlace.Tokenizer.class,
        MobilePlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}