package hotmath.gwt.cm_mobile_assignments.client;


import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;

import com.google.gwt.place.shared.Place;
import com.googlecode.mgwt.mvp.client.Animation;
import com.googlecode.mgwt.mvp.client.AnimationMapper;

public class AssAnimationMapper implements AnimationMapper {

        @Override
        public Animation getAnimation(Place oldPlace, Place newPlace) {
                if (oldPlace instanceof AboutPlace && newPlace instanceof HomePlace) {
                        return Animation.SLIDE;
                }
                return Animation.FADE;
        }
}
