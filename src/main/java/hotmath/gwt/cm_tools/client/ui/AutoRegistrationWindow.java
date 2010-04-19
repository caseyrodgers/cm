package hotmath.gwt.cm_tools.client.ui;

// import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountsAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationPreviewAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Display Admin Auto Registration of Student window
 * 
 * Provides quick and easy method of creating bulk users while using default
 * values/auto increment.
 * 
 * Also, provides hooks to upload external configuration file
 * 
 * @author casey
 * 
 */
public class AutoRegistrationWindow extends CmWindow {

    Integer adminId;
    Integer numToCreate;
    StudentModel student;
    AutoRegistrationSetup _preview;
    String uploadFileKey;

    public AutoRegistrationWindow(StudentModel student, String uploadFileKey) {
        this.student = student;
        this.uploadFileKey = uploadFileKey;
        
        setHeading("Bulk Student Registration");

        setLayout(new CenterLayout());
        Label l = new Label("Creating Preview .. please wait");
        l.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        add(l);
        setSize(580, 410);
        setResizable(false);
        drawGui();
        setModal(true);
        createPreview();
        
        setVisible(false);
    }

    Button _buttonCreate;
    Button _buttonCancel;

    private void drawGui() {
    	
        _buttonCreate = new Button("Create Students");
        
        _buttonCreate.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                createStudentAccounts();
            }
        });
        _buttonCreate.setEnabled(false);
        addButton(_buttonCreate);

        _buttonCancel = new Button("Cancel");
        _buttonCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        //getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        addButton(_buttonCancel);
    }

    private FormData formData = new FormData("-20");
    Grid<AutoRegistrationEntryGxt> _previewGrid;

    private void createForm(int total, int errors, List<AutoRegistrationEntryGxt> gridModel) {
        removeAll();

        setLayout(new BorderLayout());

        add(createInfoPanel(total,errors), new BorderLayoutData(LayoutRegion.NORTH, 85));

        ListStore<AutoRegistrationEntryGxt> store = new ListStore<AutoRegistrationEntryGxt>();
        store.add(gridModel);
        _previewGrid = new Grid<AutoRegistrationEntryGxt>(store, defineColumns());

        _previewGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _previewGrid.getSelectionModel().setFiresEvents(true);
        _previewGrid.setStripeRows(true);
        _previewGrid.setWidth(565);
        _previewGrid.setHeight(210);

        add(_previewGrid, new BorderLayoutData(LayoutRegion.CENTER));
        layout();
    }

    private Widget createInfoPanel(int total, int error) {
        String html = " <div class='detail-info' style='height: 55px;' >"
                + "     <div class='form left'>"
                + "        <div class='fld'><label>Program:</label><div>"
                + student.getProgram().getProgramDescription()
                + "&nbsp;</div></div>"
                + "        <div class='fld'><label>Pass %:</label><div>"
                + student.getPassPercent()
                + "&nbsp;</div></div>"
                + "        <div class='fld'><label>Group:</label><div>"
                + student.getGroup()
                + "&nbsp;</div></div>"
                + "     </div>"
                + "     <div class='form right'>"
                + "        <div class='fld'><label>Tutoring: </label><div>"
                + (student.getTutoringAvail() ? "Available" : "Not Available")
                + "&nbsp;</div></div>"
                + "        <div class='fld'><label>Show Work:</label><div>"
                + (student.getShowWorkRequired() ? "Required" : "Optional")
                + "&nbsp;</div></div>"
                + "     </div>"
                + " </div>"
                + "<h1 style='text-align: center;color:red;'>" +
                "Bulk Upload Problems" + 
                "</h1>";
        return new Html(html);
    }

    /** Create the preview first.  If there are any errors
     *  display the messages and stop .. if there are zero errors
     *  then begin the process of creating the users and skip showing
     *  the preview panel.
     *  
     */
    private void createPreview() {
        CatchupMathTools.setBusy(true);
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new CreateAutoRegistrationPreviewAction(student, uploadFileKey),
                new AsyncCallback<AutoRegistrationSetup>() {
                    @Override
                    public void onSuccess(AutoRegistrationSetup result) {
                        _preview = result;
                    	
                        int errorCount=0;
                        for(int i=0,t=result.getEntries().size();i<t;i++) {
                        	if(result.getEntries().get(i).getIsError()) {
                        		errorCount++;
                        	}
                        }
                        
                    	if(errorCount == 0) {
                    		createStudentAccounts();
                    	}
                    	else {
	                        /** There are records to create
	                         * 
	                         */
                    		createForm(result.getEntries().size(), errorCount, createGxtModelFromEntries(result.getEntries()));
                    		
	                        if(result.getEntries().size() - errorCount > 0)
	                        	_buttonCreate.setEnabled(true);
	                        
	                        setVisible(true);
                    	}
                        CatchupMathTools.setBusy(false);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    	CatchupMathTools.setBusy(false);
                        caught.printStackTrace();
                        CatchupMathTools.showAlert("Problem occurred while creating preview: " + caught.getMessage());
                        close();
                    }
                });
    }

    /**
     * Convert from generic model, to specific GXT model
     * 
     */
    private List<AutoRegistrationEntryGxt> createGxtModelFromEntries(List<AutoRegistrationEntry> entries) {
        List<AutoRegistrationEntryGxt> gridModel = new ArrayList<AutoRegistrationEntryGxt>();
        for (AutoRegistrationEntry e : entries) {
        	if(e.getIsError())
                gridModel.add(new AutoRegistrationEntryGxt(e));
        }
        return gridModel;
    }

    private void createStudentAccounts() {
    	CatchupMathTools.setBusy(true);
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new CreateAutoRegistrationAccountsAction(adminId, student, _preview.getEntries()),
                new AsyncCallback<AutoRegistrationSetup>() {
                    @Override
                    public void onSuccess(AutoRegistrationSetup result) {
                    	EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
                    	
                    	
                    	CatchupMathTools.setBusy(false);

                    	int cnt = result.getEntries().size() - result.getErrorCount();
                    	String msgSuccess = cnt + " bulk student " + (cnt==1?"record":"records") + " created successfully!";

                        if (result.getErrorCount() > 0) {
                        	
                            _buttonCancel.setText("Close");
                            _buttonCreate.setEnabled(false);

                            _previewGrid.getStore().removeAll();
                            _previewGrid.getStore().add(createGxtModelFromEntries(result.getEntries()));
                        	
                            int ok = result.getEntries().size() - result.getErrorCount();
                            
                            String msg = "";
                            if(ok > 0) {
                            	String msgErr = "However, there were errors while creating the other student accounts.";
                            	msg = msgSuccess + " " + msgErr;
                            }
                            else {
                            	// none successful
                            	msg = "There were errors while creating the new student accounts.";
                            }
                            msg += " Please see associated error messages.";
                            
                            CatchupMathTools.showAlert(msg);
                        } else {
                        	
                            CatchupMathTools.showAlert(msgSuccess,
                                    new CmAsyncRequestImplDefault() {
                                        @Override
                                        public void requestComplete(String requestData) {
                                            close();
                                        }
                                    });
                        }
                        layout();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    	CatchupMathTools.setBusy(false);
                        caught.printStackTrace();
                        CatchupMathTools.showAlert("Bulk Student Records FAILED: " + caught.getMessage());
                    }

                });
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig date = new ColumnConfig();
        date.setId(AutoRegistrationEntryGxt.NAME_KEY);
        date.setHeader("Student Name");
        date.setWidth(125);
        date.setSortable(true);
        date.setMenuDisabled(true);
        configs.add(date);

        ColumnConfig program = new ColumnConfig();
        program.setId(AutoRegistrationEntryGxt.PASSWORD_KEY);
        program.setHeader("Password");
        program.setWidth(125);
        program.setSortable(true);
        program.setMenuDisabled(true);
        configs.add(program);

        ColumnConfig message = new ColumnConfig();
        message.setId(AutoRegistrationEntryGxt.MESSAGE_KEY);
        message.setHeader("Message");
        message.setWidth(275);
        message.setSortable(true);
        message.setMenuDisabled(true);
        configs.add(message);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
}

class AutoRegistrationEntryGxt extends BaseModelData {

    final static String NAME_KEY = "name";
    final static String PASSWORD_KEY = "password";
    final static String MESSAGE_KEY = "message";

    public AutoRegistrationEntryGxt(AutoRegistrationEntry entry) {
        set(NAME_KEY, entry.getName());
        set(PASSWORD_KEY, entry.getPassword());

        if (entry.getIsError() == null)
            set(MESSAGE_KEY, "Pending");
        else
            set(MESSAGE_KEY, entry.getMessage());
    }

    public void setMessage(String msg) {
        set(MESSAGE_KEY, msg);
    }
}
