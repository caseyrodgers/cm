package hotmath.gwt.cm_search.client;

import hotmath.gwt.cm_search.client.places.SearchPlace;
import hotmath.gwt.cm_search.client.places.TopicPlace;
import hotmath.gwt.cm_search.client.places.TutorPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({SearchPlace.Tokenizer.class,TopicPlace.Tokenizer.class,TutorPlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}