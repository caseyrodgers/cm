package hotmath.gwt.cm_mobile_assignments.client.util;

import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.widget.ScrollPanel;

public class ChromeWorkaround {

    private static boolean isChrome = isChrome();

    // workaround for chrome bug
    // see: http://code.google.com/p/mgwt/issues/detail?id=164
    public static void maybeUpdateScroller(ScrollPanel scrollPanel) {
        // no impact on production
        // this compiles out!
        if (!MGWT.getOsDetection().isDesktop())
            return;
        if (isChrome) {
            scrollPanel.setUsePos(true);
        }
    }

    private static native boolean isChrome()/*-{
        return navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
    }-*/;

}