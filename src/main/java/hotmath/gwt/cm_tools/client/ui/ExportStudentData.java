package hotmath.gwt.cm_tools.client.ui;

import java.util.Date;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

import com.google.gwt.user.client.Cookies;

/**
 * Export Student Data UI
 * 
 * generate Excel spreadsheet containing data for students currently available in Grid
 * and email to provided email address.  
 * 
 * @author bob
 *
 */
public class ExportStudentData extends LayoutContainer {
	
	private CmWindow exportWindow;

	private FieldSet exportFlds;
	private Integer adminUid;
	private int formHeight = 190;
	private int formWidth  = 340;

	private TextField<String> emailAddr;
	
	public ExportStudentData(Integer adminUid) {

		this.adminUid = adminUid;

		exportWindow = new CmWindow();
		exportWindow.add(exportForm());
		
		setForm();

	}
	
	private FormPanel exportForm() {
		FormPanel fp = new FormPanel();
		fp.setLabelWidth(80);
		fp.setHeight(formHeight);
		fp.setFooter(true);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setLayout(new FormLayout());

        exportFlds = new FieldSet();
        
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(fp.getLabelWidth());
		fl.setDefaultWidth(200);
		
		exportFlds.setLayout(fl);

        emailAddr = new TextField<String>();  
        emailAddr.setFieldLabel("Email");
        emailAddr.setAllowBlank(false);
        emailAddr.setId("email");
        emailAddr.setEmptyText("-- email address --");
		if (haveEmailAddrCookie()) {
		    emailAddr.setValue(readEmailAddrCookie());
		}
		emailAddr.setValidator(new ValidTypeValidator(ValidType.EMAIL));
		emailAddr.setToolTip("spreadsheet will be emailed to this address");
		emailAddr.setMaxLength(300);
		exportFlds.add(emailAddr);

		exportWindow.setHeading("Export Student Data");
		exportWindow.setWidth(formWidth+10);
		exportWindow.setHeight(formHeight+20);
		exportWindow.setLayout(new FitLayout());
		exportWindow.setResizable(false);
		exportWindow.setDraggable(true);
		exportWindow.setModal(true);

		fp.add(exportFlds);
		
		fp.add(getDescription());

		Button cancelBtn = cancelButton();
        cancelBtn.addStyleName("cancel-button");
        
		Button saveBtn = exportButton(exportFlds, fp);
		saveBtn.addStyleName("save-button");
		
		fp.setButtonAlign(HorizontalAlignment.RIGHT);
        fp.addButton(saveBtn);
        fp.addButton(cancelBtn);
        return fp;
	}

	private void setForm() {
		exportWindow.show();
	}

	private Button cancelButton() {
		Button cancelBtn = new Button("Cancel", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
                exportWindow.close();
	        }  
	    });
		return cancelBtn;
	}

	private Button exportButton(final FieldSet fs, final FormPanel fp) {
		Button exportBtn = new Button("Export", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {

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
	
	private LayoutContainer getDescription() {
		LayoutContainer lc = new LayoutContainer();
        lc.add(new Html("An Excel spreadsheet containing student details and selected report card data for your currently displayed students will be generated and sent from 'registration@hotmath.com' to the email address you provide."));
        return lc;
	}

	private void exportStudentDataRPC(final Integer adminUid, final String emailAddr) {
		new RetryAction<StringHolder> () {
		    @Override
		    public void attempt() {
		        CmBusyManager.setBusy(true);
		        ExportStudentsAction action =
		        	new ExportStudentsAction(adminUid, new GetStudentGridPageAction());
		        action.setEmailAddress(emailAddr);
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