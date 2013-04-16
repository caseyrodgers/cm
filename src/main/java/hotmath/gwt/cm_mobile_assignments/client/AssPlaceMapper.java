package hotmath.gwt.cm_mobile_assignments.client;



import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ HomePlace.Tokenizer.class, AboutPlace.Tokenizer.class})
public interface AssPlaceMapper extends PlaceHistoryMapper {
}