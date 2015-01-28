package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.ExportGradebookAction;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Export Grade Book UI
 * 
 * generate Excel spreadsheet containing Grade Book data for selected Group's Assignments
 * and email to provided email address.  
 * 
 * @author bob
 *
 */
public class ExportGradebooksDialog extends SimplePanel {
	
	private GWindow exportWindow;

	private FieldSet exportFlds;
	private Integer adminUid;
	private Integer groupId;
	private String groupName;
	private int formHeight = 190;
	private int formWidth  = 340;

	private TextField emailAddr;
	
	public ExportGradebooksDialog(Integer adminUid, Integer groupId, String groupName) {

		this.adminUid = adminUid;
		this.groupId = groupId;
		this.groupName = groupName;

		exportWindow = new GWindow(false);
		exportWindow.setWidget(exportForm());

		setForm();

	}
	
	private FormPanel exportForm() {
	    
	    FramedPanel frame = new FramedPanel();
	    
		FormPanel fp = new FormPanel();
		
		// fp.setLabelWidth(80);
		// fp.setHeight(formHeight);
		// fp.gsetFooter(true);
		// fp.setFrame(false);
		frame.setHeaderVisible(false);
		// fp.setBodyBorder(false);
		// fp.setIconStyle("icon-form");
		// fp.setButtonAlign(HorizontalAlignment.CENTER);
		// fp.setLayout(new FormLayout());

        exportFlds = new FieldSet();
        
        emailAddr = new TextField();  
        //emailAddr.setFieldLabel("Email");
        emailAddr.setAllowBlank(false);
        emailAddr.setId("email");
        emailAddr.setEmptyText("-- email address --");
		if (haveEmailAddrCookie()) {
		    emailAddr.setValue(readEmailAddrCookie());
		}
		// emailAddr.setValidator(new ValidTypeValidator(ValidType.EMAIL));
		emailAddr.setToolTip("spreadsheet will be emailed to this address");
		// emailAddr.setMaxLength(300);
		exportFlds.add(emailAddr);

		exportWindow.setHeadingText("Export Grade Book");
		exportWindow.setWidth(formWidth+10);
		exportWindow.setHeight(formHeight+20);
		exportWindow.setResizable(false);
		exportWindow.setDraggable(true);
		exportWindow.setModal(true);

		fp.add(exportFlds);
		
		fp.add(getDescription());

		TextButton cancelBtn = cancelButton();
        cancelBtn.addStyleName("cancel-button");
        
		TextButton saveBtn = exportButton(exportFlds, fp);
		saveBtn.addStyleName("save-button");
		
		// fp.setButtonAlign(HorizontalAlignment.RIGHT);
        frame.addButton(saveBtn);
        frame.addButton(cancelBtn);

        return fp;
	}

	private void setForm() {
		exportWindow.show();
	}

	private TextButton cancelButton() {
		TextButton cancelBtn = new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                exportWindow.close();
	        }  
	    });
		return cancelBtn;
	}

	private TextButton exportButton(final FieldSet fs, final FormPanel fp) {
		TextButton exportBtn = new TextButton("Export", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
	    		String emailAddress = emailAddr.getValue();
	    		
	    		if (emailAddr.isValid() == false) {
	    			emailAddr.focus();
	    			return;
	    		}
	    		saveEmailAddrCookie(emailAddress);

	    		exportGradebooksRPC(emailAddress);
	    		
	        }
	    });
		return exportBtn;
	}
	
	private SimplePanel getDescription() {
	    SimplePanel lc = new SimplePanel();
		groupName = (groupName == null) ? "selected" : groupName;
        lc.setWidget(new HTML("An Excel spreadsheet containing grade book data for your " + groupName + " group will be generated and sent from 'registration@catchupmath.com' to the email address you provide."));
        return lc;
	}

	private void exportGradebooksRPC(final String emailAddr) {
		new RetryAction<StringHolder> () {
		    @Override
		    public void attempt() {
		        CmBusyManager.setBusy(true);
                DateRangePanel dateRange = DateRangePanel.getInstance();
                Date fromDate, toDate;
                if (dateRange.isDefault()) {
                	fromDate = null;
                	toDate = null;
                }
                else {
                	fromDate = dateRange.getFromDate();
                	toDate = dateRange.getToDate();
                }
		        ExportGradebookAction action =
		        	new ExportGradebookAction(adminUid, groupId, groupName, StudentGridPanel.instance.getPageAction());
		        action.setEmailAddress(emailAddr);
            	action.setFilterMap(StudentGridPanel.instance.getPageAction().getFilterMap());
            	action.setFromDate(fromDate);
            	action.setToDate(toDate);
		        setAction(action);
		        CmRpcCore.getCmService().execute(action,this);
		    }
		    
            public void oncapture(StringHolder msg) {
                CmBusyManager.setBusy(false);
                CatchupMathTools.showAlert(msg.getResponse());
                exportWindow.close();
            }

        }.register();

	}

    private String readEmailAddrCookie() {
        String emailAddr = Cookies.getCookie("cm_export_email");
        return emailAddr;
    }

    private boolean haveEmailAddrCookie() {
    	return readEmailAddrCookie() != null;
    }

    private void saveEmailAddrCookie(String email) {
    	Date date = new Date();
        long time = date.getTime() + (1000 * 60 * 60 * 24 * 3000);
        date.setTime(time);
        Cookies.setCookie("cm_export_email", email, date);
    }

}