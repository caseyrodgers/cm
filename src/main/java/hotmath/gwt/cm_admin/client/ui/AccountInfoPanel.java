package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
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
                CmServiceAsync s = CmRpcCore.getCmService();
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

        if ((ai.getTotalStudents() - ai.getCountCommunityStudents()) > ai.getMaxStudents()) {
            if (!haveDisplayedOverLimitMsg) {
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

        setExpirationDateStyle(ai);

        setAccountInfoModel(ai);

        DateRangePanel.getInstance().setDefaultFromDate(ai.getAccountCreateDate());

        CmLogger.info("AccountInfoPanel: student info read succesfully");
    }

	DateTimeFormat dateFmt = DateTimeFormat.getFormat("yyyy-MM-dd");
	long MSEC_IN_A_DAY = 1000L * 60L * 60L * 24L;

    private void setExpirationDateStyle(AccountInfoModel ai) {
    	String expDateStr = ai.getExpirationDate();
    	String curDateStr = ai.getCurrentDate();
    	Date expDate = null;
    	Date curDate = null;
    	try {
        	expDate = dateFmt.parse(expDateStr);
        	curDate = dateFmt.parse(curDateStr);
        	long diff = expDate.getTime() - curDate.getTime();
        	long numDays = diff / MSEC_IN_A_DAY;
        	if (numDays < 16) {
        		ai.setExpirationDateStyle("fld-warn");
        	}
        	else {
        		ai.setExpirationDateStyle("fld");
        	}
    	}
    	catch (Exception e) {
    		if (expDate == null) CmLogger.error("date parse failed: expDate: " + expDateStr);
    		else CmLogger.error("date parse failed: curDate: " + curDateStr);
    	}
	}

	@Override
    public void refreshData() {
        getAccountInfoRPC(cmAdminModel.getUid());
    }	
}
