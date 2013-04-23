package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmTemplateFormaters;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * <code>AccountInfoPanel</code> displays account info in a read-only panel
 *
 * @author bob
 *
 */
public class AccountInfoPanel extends FlowLayoutContainer implements CmAdminDataRefresher {
	
	private AccountInfoModel model;
	private CmAdminModel cmAdminModel;
	private Boolean haveDisplayedOverLimitMsg = false;
	
	@FormatterFactories(@FormatterFactory(factory = CmTemplateFormaters.class, name = "nullChecker"))
	interface InfoLoaderTemplate extends XTemplates {
		@XTemplate(source="AccountInfoPanel_InfoLoader.html")
		SafeHtml renderAdminInfo(AccountInfoModel adminInfo);
	}

	
	SimpleContainer header = new SimpleContainer();
	public AccountInfoPanel(CmAdminModel cmAdminMdl) {
	
		
		cmAdminModel = cmAdminMdl;
		
		setStyleName("account-info");
		header.setStyleName("account-info-panel");
		
		
//		InfoLoaderTemplate loaderTemplate = GWT.create(InfoLoaderTemplate.class);
//		HTML html = new HTML(loaderTemplate.renderAdminInfo(new AccountInfoModelImplPojo()));
//		html.setVisible(false);
//		hp.add(html);
		
		CmAdminDataReader.getInstance().addReader(this);

		add(header);
	}
	
	
	public AccountInfoModel getModel() {
	    return this.model;
	}

	/** Set the Account Info header fields
	 * 
	 * @param model
	 */
	public void setAccountInfoModel(AccountInfoModel model) {
		this.model = model;
		InfoLoaderTemplate iLoader = GWT.create(InfoLoaderTemplate.class);
		String html = iLoader.renderAdminInfo(model).asString();
		header.setWidget(new HTML(html));
	}
	
	public AccountInfoModel getAccountInfoModel() {
		return this.model;
	}

    protected void getAccountInfoRPC(final Integer uid) {
        new RetryAction<AccountInfoModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CmServiceAsync s = CmShared.getCmService();
                GetAccountInfoForAdminUidAction action = new GetAccountInfoForAdminUidAction(uid);
                setAction(action);
                CmLogger.info("AccountInfoPanel: reading admin info RPC");
                s.execute(action,this);
            }

            public void oncapture(AccountInfoModel ai) {
                CmBusyManager.setBusy(false);
                setAccountInfo(ai);
            }
        }.register();        
    }
    
    public void setAccountInfo(AccountInfoModel ai) {
        StringBuilder sb = new StringBuilder();
        sb.append("Manage ").append(ai.getSchoolName()).append(" Students");
        // _gridContainer.setHeading(sb.toString());

        if((ai.getTotalStudents() - ai.getCountFreeStudents() )> ai.getMaxStudents()) {
            if(!haveDisplayedOverLimitMsg) {
                String msg = "Your student registration now exceeds the licensed total. " +
                             "We will contact you soon about upgrading your license, or you " +
                             "may wish to unregister students no longer active.  Thank you " +
                             "for using Catchup Math!";
                CmMessageBox.showAlert("Number of Students Exceeds License", msg);
                haveDisplayedOverLimitMsg = true;
            }
            ai.setStudentCountStyle("fld-warn");
        }
        else {
            ai.setStudentCountStyle("fld");
        }
        setAccountInfoModel(ai);

        DateRangePanel.getInstance().setDefaultFromDate(ai.getAccountCreateDate());

        CmLogger.info("AccountInfoPanel: student info read succesfully");
    }
    
    //@Override
    public void refreshData() {
        getAccountInfoRPC(cmAdminModel.getUid());
    }	
}
