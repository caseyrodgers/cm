package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;

public class TutorCallbackMobileDefault extends TutorCallbackDefault {
    @Override
    /** Scroll to bottom of screen is problematic on mobile */
    public void scrollToBottomOfScrollPanel() {
        // nothing
    }
}
