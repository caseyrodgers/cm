package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudyProgramExt;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.cm_tools.client.model.SubjectModelProperties;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CCSSGradeLevel;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSLevelsAction;
import hotmath.gwt.shared.client.rpc.action.ExportStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentActivityAction;

import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.LabelProviderSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
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
	private MyFieldSet levelField;
	private ListStore<CCSSGradeLevel> levelStore;
	private ComboBox<CCSSGradeLevel> levelCombo;
	private String _levelName;
	private Integer adminUid;
	private int formHeight = 190;
	private int formWidth  = 400;

	private TextField emailAddr;

	interface LevelProperties extends PropertyAccess<CCSSGradeLevel> {
		    ModelKeyProvider<CCSSGradeLevel> name();
		 
		    LabelProvider<CCSSGradeLevel> label();
    }
    static LevelProperties _levelProps = GWT.create(LevelProperties.class);

	  
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

        exportFlds = new MyFieldSet("Export To?");

        emailAddr = new TextField();  
        emailAddr.setAllowBlank(false);
        emailAddr.setId("email");
        emailAddr.setEmptyText("-- email address --");
		if (haveEmailAddrCookie()) {
		    emailAddr.setValue(readEmailAddrCookie());
		}
		emailAddr.addValidator(new MyValidatorDef(MyValidators.EMAIL));
		emailAddr.setToolTip("spreadsheet will be emailed to this address");
		//emailAddr.setMaxLength(300);
		exportFlds.addThing(new MyFieldLabel(emailAddr, "Email", 100,250));

        levelField = new MyFieldSet("Strand");
        levelStore = new ListStore<CCSSGradeLevel>(_levelProps.name());
        getStandardLevelsRPC(levelStore);
        levelCombo = levelCombo(levelStore);
        levelField.addThing(new MyFieldLabel(levelCombo, "Strand", 100, 250));

		exportWindow.setHeadingText("Export Student Data");
		exportWindow.setWidth(formWidth+10);
		exportWindow.setHeight(formHeight+110);
		exportWindow.setResizable(false);
		exportWindow.setDraggable(true);
		exportWindow.setModal(true);

		vMain.add(exportFlds);
		vMain.add(levelField);
		
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

	private ComboBox<CCSSGradeLevel> levelCombo(ListStore<CCSSGradeLevel> store) {
        LabelProviderSafeHtmlRenderer<CCSSGradeLevel> renderer = new LabelProviderSafeHtmlRenderer<CCSSGradeLevel>(_levelProps.label()) {
            public SafeHtml render(CCSSGradeLevel level) {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                return sb.appendEscaped(level.getName()).toSafeHtml();
            }
        };
        ComboBox<CCSSGradeLevel> combo = new ComboBox<CCSSGradeLevel>(new ComboBoxCell<CCSSGradeLevel>(store, _levelProps.label(), renderer));
        combo.setForceSelection(false);
        combo.setEditable(false);
        combo.setAllowBlank(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setStore(store);
        combo.setTitle("Select a Strand");
        combo.setId("level-combo");
        combo.setTypeAhead(true);
        combo.setSelectOnFocus(true);
        combo.setEmptyText("-- select a strand --");
        combo.setWidth(280);
        combo.addSelectionHandler(new SelectionHandler<CCSSGradeLevel>() {

            @Override
            public void onSelection(SelectionEvent<CCSSGradeLevel> event) {
                CCSSGradeLevel level = event.getSelectedItem();
                _levelName = level.getName();
            }
        });

        return combo;
	}

    protected void getStandardLevelsRPC(final ListStore<CCSSGradeLevel> store) {

        CmBusyManager.setBusy(true);

        new RetryAction<CmList<CCSSGradeLevel>>() {
            public void oncapture(CmList<CCSSGradeLevel> list) {
                try {
                    store.clear();
                    store.addAll(list);
                    exportWindow.show();
                } catch (Exception e) {
                    Log.error("Error: " + list.size(), e);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }

            @Override
            public void attempt() {
                CmServiceAsync s = CmShared.getCmService();

                CCSSLevelsAction action = new CCSSLevelsAction();
                setAction(action);
                s.execute(action, this);
            }
        }.register();
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
		        action.setLevelName(_levelName);
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