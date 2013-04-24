package hotmath.gwt.cm_mobile_assignments.client;

import hotmath.gwt.cm_mobile_assignments.client.place.AssProblemPlace;
import hotmath.gwt.cm_mobile_assignments.client.place.AssignmentPlace;
import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;
import hotmath.gwt.cm_mobile_assignments.client.place.ShowWorkPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({ HomePlace.Tokenizer.class, AboutPlace.Tokenizer.class, AssignmentPlace.Tokenizer.class, AssProblemPlace.Tokenizer.class, ShowWorkPlace.Tokenizer.class })
public interface AssPlaceMapper extends PlaceHistoryMapper {
}