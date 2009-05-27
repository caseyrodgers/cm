package hotmath.gwt.cm_admin.client.ui;

import java.util.List;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.CmAdminModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.store.ListStore;
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
		hp.setStyleName("account-info");
		// left:640px;
		// position:absolute;
	
		StringBuilder sb = new StringBuilder();  
		sb.append("<div 'style='line-height: 20px; font-size: 13px; width: 800px'>");
		
		sb.append("<div style='float:right; width:280px>'");
		sb.append("<p><b>Admin:</b> {admin-user-name}</p>");
		sb.append("<p><b>Last login:</b> {last-login}</p>");
		sb.append("</div");

		sb.append("<div style='float: left; margin-right:20px; width:280px'>");
		sb.append("<p><b>School:</b> {school-name}</p>");
		sb.append("<p><b>Contact:</b> {school-user-name}</p>");
		sb.append("<p><b>Status:</b> {status}</p>");
		sb.append("<p><b>Expires:</b> {expiration-date}</p>");
		sb.append("<p><b>Tutoring:</b> {has-tutoring}</p>");
		sb.append("</div></div>");
		
		template = XTemplate.create(sb.toString());  
		html = new HTML();
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
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}
}
