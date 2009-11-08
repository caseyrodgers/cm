package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class AccountInfoPanel extends LayoutContainer implements CmAdminDataRefresher {
	
	private XTemplate template;
	private HTML html;
	private AccountInfoModel model;
	private CmAdminModel cmAdminModel;

	public AccountInfoPanel(CmAdminModel cmAdminMdl) {
		
		cmAdminModel = cmAdminMdl;

		LayoutContainer hp = new LayoutContainer();
		hp.setAutoWidth(true);
		//hp.setSpacing(10);
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
        sb.append("  <div class='fld'><label>Previous admin login:</label><div>{last-login}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Student count:</label><div>{total-students}&nbsp;</div></div>");
        sb.append("</div>");		
		sb.append("</div>");
		
		template = XTemplate.create(sb.toString());  
		html = new HTML();
		html.setVisible(false);
		html.setHTML(sb.toString());
		hp.add(html);
		

		add(hp);
		
		CmAdminDataReader.getInstance().addReader(this);
	}
	
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setStyleName("account-info");
	}

	/** Set the Account Info header fields
	 * 
	 * @param model
	 */
	public void setAccountInfoModel(AccountInfoModel model) {
		this.model = model;
		
		template.overwrite(html.getElement(), Util.getJsObject(this.model));
		
		html.setVisible(true);
	}
	
	public AccountInfoModel getAccountInfoModel() {
		return this.model;
	}

    protected void getAccountInfoRPC(Integer uid) {
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");

        Log.info("AccountInfoPanel: reading student info RPC");
        s.getAccountInfoForAdminUid(uid, new AsyncCallback<AccountInfoModel>() {

            public void onSuccess(AccountInfoModel ai) {
                StringBuilder sb = new StringBuilder();
                sb.append("Manage ").append(ai.getSchoolName()).append(" Students");
                // _gridContainer.setHeading(sb.toString());

                setAccountInfoModel(ai);

                Log.info("AccountInfoPanel: student info read succesfully");
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                caught.printStackTrace();  // quite
                //CatchupMathAdmin.showAlert(msg);
            }
        });
    }

    //@Override
    public void refreshData() {
        getAccountInfoRPC(cmAdminModel.getId());
    }	
}
