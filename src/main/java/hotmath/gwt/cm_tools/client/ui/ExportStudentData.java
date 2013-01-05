package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Export Student Data UI
 * 
 * generate Excel spreadsheet containing data for students currently available in Grid
 * and email to provided email address.  
 * 
 * @author bob
 *
 */
public class ExportStudentData extends SimpleContainer {
	
	private GWindow exportWindow;

	private MyFieldSet exportFlds;
	private Integer adminUid;
	private int formHeight = 190;
	private int formWidth  = 400;

	private TextField emailAddr;
	
	public ExportStudentData(Integer adminUid) {

		this.adminUid = adminUid;

		exportWindow = new GWindow(false);
		
		FramedPanel fp = new FramedPanel();
		fp.setHeaderVisible(false);
		fp.setWidget(exportForm());
		exportWindow.setWidget(fp);
		
		
		setForm();

	}
	
	private FormPanel exportForm() {
		FormPanel fp = new FormPanel();
		
		VerticalLayoutContainer vMain = new VerticalLayoutContainer();
		fp.setWidget(vMain);
		//fp.setFooter(true);
		//fp.setFrame(false);
		//fp.setHeaderVisible(false);
		//fp.setBodyBorder(false);
		//fp.setIconStyle("icon-form");
		//fp.setButtonAlign(HorizontalAlignment.CENTER);
		//fp.setLayout(new FormLayout());

        exportFlds = new MyFieldSet("Export To?");

        emailAddr = new TextField();  
        //emailAddr.setFieldLabel("Email");
        emailAddr.setAllowBlank(false);
        emailAddr.setId("email");
        emailAddr.setEmptyText("-- email address --");
		if (haveEmailAddrCookie()) {
		    emailAddr.setValue(readEmailAddrCookie());
		}
		emailAddr.addValidator(new MyValidatorDef(MyValidators.EMAIL));
		emailAddr.setToolTip("spreadsheet will be emailed to this address");
		// emailAddr.setMaxLength(300);
		exportFlds.addThing(new MyFieldLabel(emailAddr, "Email", 100,250));

		exportWindow.setHeadingText("Export Student Data");
		exportWindow.setWidth(formWidth+10);
		exportWindow.setHeight(formHeight+40);
		exportWindow.setResizable(false);
		exportWindow.setDraggable(true);
		exportWindow.setModal(true);

		vMain.add(exportFlds);
		
		vMain.add(getDescription());

		TextButton cancelBtn = cancelButton();
        cancelBtn.addStyleName("cancel-button");
        
		TextButton saveBtn = exportButton(exportFlds, fp);
		saveBtn.addStyleName("save-button");
		
        exportWindow.addButton(saveBtn);
        exportWindow.addButton(cancelBtn);
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

	    		exportStudentDataRPC(adminUid, emailAddress);
	    		
	        }
	    });
		return exportBtn;
	}
	
	private Widget getDescription() {
        return new HTML("An Excel spreadsheet containing student details and selected report card data for your currently displayed students will be generated and sent from 'registration@hotmath.com' to the email address you provide.");
	}

	private void exportStudentDataRPC(final Integer adminUid, final String emailAddr) {
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
		        ExportStudentsAction action =
		        	new ExportStudentsAction(adminUid, StudentGridPanel.instance.getPageAction());
		        action.setEmailAddress(emailAddr);
            	action.setFilterMap(StudentGridPanel.instance.getPageAction().getFilterMap());
            	action.setFromDate(fromDate);
            	action.setToDate(toDate);
		        setAction(action);
		        CmShared.getCmService().execute(action,this);
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