package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

/**
 * <code>AccountInfoPanel</code> displays account info in a read-only panel
 *
 * @author bob
 *
 */
public class AccountInfoPanel extends LayoutContainer implements CmAdminDataRefresher {
	
	private XTemplate template;
	private HTML html;
	private AccountInfoModel model;
	private CmAdminModel cmAdminModel;
	private Boolean haveDisplayedOverLimitMsg = false;
	private Boolean isTutoringEnabled = false;

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
		sb.append("  <div class='fld'><label>Live Tutoring:</label><div>{has-tutoring}&nbsp;{tutoring-minutes-label}</div></div>");
		sb.append("</div>");
        sb.append("<div class='form right'>");
        sb.append("  <div class='fld'><label>Account login name:</label><div>{admin-user-name}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Previous admin login:</label><div>{last-login}&nbsp;</div></div>");
        sb.append("  <div class='{student-count-style}'><label>Student count:</label><div>{total-students}&nbsp;</div></div>");
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
	
	
	public AccountInfoModel getModel() {
	    return this.model;
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

    protected void getAccountInfoRPC(final Integer uid) {
        
        
        new RetryAction<AccountInfoModel>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
                GetAccountInfoForAdminUidAction action = new GetAccountInfoForAdminUidAction(uid);
                setAction(action);
                Log.info("AccountInfoPanel: reading student info RPC");
                s.execute(action,this);
            }

            public void oncapture(AccountInfoModel ai) {
                CmBusyManager.setBusy(false);
                
                StringBuilder sb = new StringBuilder();
                sb.append("Manage ").append(ai.getSchoolName()).append(" Students");
                // _gridContainer.setHeading(sb.toString());

                if(ai.getTotalStudents() > ai.getMaxStudents()) {
                    if(!haveDisplayedOverLimitMsg) {
                        String msg = "Your student registration now exceeds the licensed total. " +
                                     "We will contact you soon about upgrading your license, or you " +
                                     "may wish to unregister students no longer active.  Thank you " +
                                     "for using Catchup Math!";
                        CatchupMathTools.showAlert("Number of Students Exceeds License", msg);
                        haveDisplayedOverLimitMsg = true;
                    }
                    ai.setStudentCountStyle("fld-warn");
                }
                else {
                    ai.setStudentCountStyle("fld");

                }
                
                if(ai.getIsTutoringEnabled()) {
                    //ai.set(AccountInfoModel.TUTORING_MINUTES_LABEL, "(" + getTutoringRemaingLabel(ai.getTutoringMinutes()) + " remaining)");
                    ai.set(AccountInfoModel.TUTORING_MINUTES_LABEL, "");
                }
                
                setAccountInfoModel(ai);

                
                Log.info("AccountInfoPanel: student info read succesfully");

            }
        }.attempt();        
    }
    
    private String getTutoringRemaingLabel(int mins) {
        String label="";
        if(mins == 0) {
            label="zero minutes";
        }
        else if(mins < 60) {
            label = mins + (mins==1?" min":" mins");
        }
        else {
            int h = (mins / 60);
            label = "" + h + (h==1?" hour":" hours");
        }
        return label;
    }
    

    //@Override
    public void refreshData() {
        getAccountInfoRPC(cmAdminModel.getId());
    }	
}
