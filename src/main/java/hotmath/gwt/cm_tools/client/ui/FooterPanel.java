package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.shared.client.CmShared;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

public class FooterPanel extends LayoutContainer {

	public FooterPanel() {
		setStyleName("footer-panel");
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		String html = "<ul class='h-menu'>";

		if (CmShared.getQueryParameter("debug") != null) {
			html += "<li><a href='javascript:void(0)' onclick='resetProgram_Gwt();return false;'>Reset</a></li>";
			html += "<li><a href='javascript:void(0)' onclick='showPrescriptionData_Gwt();return false;'>prescription data</a></li>";
			html += "<li><a href='javascript:void(0)' onclick='showPrescriptionSession_Gwt();return false;'>prescription sessions</a></li>";
			html += "<li><a href='javascript:void(0)' onclick='startAutoTest_Gwt();return false;'>Auto Test</a></li>";
			html += "<li>CM_HOME_URL: " + CmShared.CM_HOME_URL + "</li>";
		}

		html += "</ul>" + "<div>Brought to you by Hotmath.com</div>";

		add(new Html(html));
	}

	private static void showPrescriptionSession_Gwt() {
		if (UserInfo.getInstance() == null
				|| UserInfo.getInstance().getRunId() == 0) {
			Window.alert("No user prescription");
			return;
		}
		String url = CmShared.CM_HOME_URL
				+ "/assets/util/_get_prescription.jsp?run_id="
				+ UserInfo.getInstance().getRunId();
		Window.open(url, "_new", "height=480,width=640,status=yes,scrollbars=1");

	}

	private static void showPrescriptionData_Gwt() {
		if (UserInfo.getInstance() == null
				|| UserInfo.getInstance().getRunId() == 0) {
			Window.alert("No user prescription");
			return;
		}
		String url = CmShared.CM_HOME_URL
				+ "/assets/util/_get_assessment_data.jsp?run_id="
				+ UserInfo.getInstance().getRunId();
		Window.open(url, "_blank", "height=480,width=640,status=yes,scrollbars=1");
	}

	public static void startAutoTest_Gwt() {

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
			    AutoTestWindow.getInstance().setVisible(true);
                AutoTestWindow.getInstance().startAutoTest();
			}

			@Override
			public void onFailure(Throwable reason) {
				Window.alert("Error loading testing logic: "
						+ reason.getLocalizedMessage());
			}
		});
	}

	/**
	 * Define JSNI methods to expose feedback services
	 * 
	 */
	static private native void publishNative() /*-{
		$wnd.showPrescriptionData_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::showPrescriptionData_Gwt();
		$wnd.showPrescriptionSession_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::showPrescriptionSession_Gwt();
		$wnd.startAutoTest_Gwt = @hotmath.gwt.cm_tools.client.ui.FooterPanel::startAutoTest_Gwt();
	}-*/;

	static {
		publishNative();
	}

}
