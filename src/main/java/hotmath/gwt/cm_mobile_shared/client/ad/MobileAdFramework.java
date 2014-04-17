package hotmath.gwt.cm_mobile_shared.client.ad;

import com.google.gwt.user.client.Timer;

/** Provide an external interface for displaying mobile ads
 * 
 * @author casey
 *
 */
public class MobileAdFramework {
	
	private static final int WAIT_TIME = 10000;

	static MobileAdFramework __instance;
	
	Timer _timer;	
	public MobileAdFramework() {
		_timer = new Timer() {
			@Override
			public void run() {
				if(checkForAd()) {
					_timer.schedule(WAIT_TIME);
				}
			}
		};
		_timer.schedule(WAIT_TIME);
	}

	int _adCount=0;
	protected boolean checkForAd() {
		jsni_showAlert("This is advertisement: " +  ++_adCount);
		return false;
	}

	public void showAlert(String message) {
		jsni_showAlert(message);
	}
	
	native private void jsni_showAlert(String message) /*-{
	    $wnd.alertify.alert(message,function(e) {
	        @hotmath.gwt.cm_mobile_shared.client.ad.MobileAdFramework::scheduleNextAd()();
	    });
	}-*/;


	public static MobileAdFramework getInstance() {
		if(__instance == null) {
			__instance = new MobileAdFramework();
		}
		return  __instance;
	}
	
	private static void scheduleNextAd() {
		getInstance()._timer.schedule(WAIT_TIME);
	}

}
