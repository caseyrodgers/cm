package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.CmAdminModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class AccountInfoPanel extends LayoutContainer {
	
	private XTemplate template;
	private HTML html;
	private AccountInfoModel model;
	private CmAdminModel cmAdminModel;

	public AccountInfoPanel(CmAdminModel cmAdminMdl) {
		
		cmAdminModel = cmAdminMdl;
		
		System.out.println("AccountInfoPanel: admin uid: " + cmAdminMdl.getId());
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.setAutoWidth(true);
		hp.setSpacing(10);
		hp.setBorders(false);
		hp.setStyleName("account-info-panel");
	
		StringBuilder sb = new StringBuilder();
		sb.append("<div class='account-info'>");
        sb.append("<div class='form school-name-form'>");
        sb.append("  <div class='fld'><label>School:</label><div>{school-name}&nbsp;</div></div>");
        sb.append("</div>");
		sb.append("<div class='form left'>");
		sb.append("  <div class='fld'><label>Administrator:</label><div>{school-user-name}&nbsp;</div></div>");
		sb.append("  <div class='fld'><label>Maximum Students:</label><div> {max-students}&nbsp;</div></div>");
		sb.append("  <div class='fld'><label>Expires:</label><div> {expiration-date}&nbsp;</div></div>");
		sb.append("  <div class='fld'><label>Live Tutoring:</label><div>{has-tutoring}&nbsp;</div></div>");
		sb.append("</div>");
        sb.append("<div class='form right'>");
        sb.append("  <div class='fld'><label>Account login name:</label><div>{admin-user-name}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Last login:</label><div>{last-login}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Student count:</label><div>{total-students}&nbsp;</div></div>");
        sb.append("</div>");		
		sb.append("</div>");
		
		template = XTemplate.create(sb.toString());  
		html = new HTML();
		html.setVisible(false);
		html.setHTML(sb.toString());
		hp.add(html);
		
		//TODO: add "Details" button or tool tip

		add(hp);
	}
	
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setStyleName("account-info");

		getAccountInfoRPC(cmAdminModel.getId());
	}

	public void setAccountInfoModel(AccountInfoModel model) {
		this.model = model;
		
		template.overwrite(html.getElement(), Util.getJsObject(this.model));  
	}

	protected void getAccountInfoRPC(Integer uid) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.getAccountInfoForAdminUid(uid, new AsyncCallback <AccountInfoModel>() {

			public void onSuccess(AccountInfoModel result) {
				setAccountInfoModel(result);
				html.setVisible(true);
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}
}
