package hotmath.gwt.cm_tools.client.ui;

// import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

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
public class AutoRegistrationWindow extends GWindow {

    Integer adminId;
    Integer numToCreate;
    StudentModelI student;
    AutoRegistrationSetup _preview;
    String uploadFileKey;
    
    static AutoRegistrationEntryModelProperties __propsAutoReg = GWT.create(AutoRegistrationEntryModelProperties.class);

    public AutoRegistrationWindow(StudentModelI student, String uploadFileKey) {
        super(false);
        this.student = student;
        this.uploadFileKey = uploadFileKey;
        setPixelSize(580, 410);
        
        CenterLayoutContainer centerLayout = new CenterLayoutContainer();
        setWidget(centerLayout);
        
        setHeadingText("Bulk Student Registration");
        
        Label l = new Label("Creating Preview .. please wait");
        l.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        centerLayout.setWidget(l);
        
        setResizable(false);
        drawGui();
        setModal(true);
        createPreview();
        
        setVisible(false);
    }

    TextButton _buttonCreateXXX;
    TextButton _buttonClose;

    private void drawGui() {
        _buttonClose = new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
            }
        });
        addButton(_buttonClose);
    }

    Grid<AutoRegistrationEntryModel> _previewGrid;

    private void createForm(int total, int errors, List<AutoRegistrationEntryModel> gridModel) {
        try {
            clear();
    
            BorderLayoutContainer borderLayout = new BorderLayoutContainer();
            setWidget(borderLayout);
    
            borderLayout.setNorthWidget(createInfoPanel(total,errors), new BorderLayoutData(85));
    
            ListStore<AutoRegistrationEntryModel> store = new ListStore<AutoRegistrationEntryModel>(__propsAutoReg.id());
            store.addAll(gridModel);
            _previewGrid = new Grid<AutoRegistrationEntryModel>(store, defineColumns());
    
            _previewGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
           //_previewGrid.getSelectionModel().setFiresEvents(true);
            _previewGrid.getView().setStripeRows(true);
            _previewGrid.setWidth(565);
            _previewGrid.setHeight(210);
    
            borderLayout.setCenterWidget(_previewGrid);
            forceLayout();
        }
        catch(Exception e) {
            Log.error("Error creating Auto Registration window", e);
        }
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
                + (student.getSettings().getTutoringAvailable() ? "Available" : "Not Available")
                + "&nbsp;</div></div>"
                + "        <div class='fld'><label>Show Work:</label><div>"
                + (student.getSettings().getShowWorkRequired() ? "Required" : "Optional")
                + "&nbsp;</div></div>"
                + "     </div>"
                + " </div>"
                + "<h1 style='text-align: center;color:red;'>" +
                "Bulk Upload Problems: Please fix the file and try again." + 
                "</h1>";
        return new HTML(html);
    }

    /** Create the preview first.  If there are any errors
     *  display the messages and stop .. if there are zero errors
     *  then begin the process of creating the users and skip showing
     *  the preview panel.
     *  
     */
    private void createPreview() {
        CatchupMathTools.setBusy(true);
        CmServiceAsync s = CmRpcCore.getCmService();
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
	                        /** There are errors to display
	                         * 
	                         */
                    		createForm(result.getEntries().size(), errorCount, createGxtModelFromEntries(result.getEntries()));
                    		
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
    private List<AutoRegistrationEntryModel> createGxtModelFromEntries(List<AutoRegistrationEntry> entries) {
        List<AutoRegistrationEntryModel> gridModel = new ArrayList<AutoRegistrationEntryModel>();
        for (AutoRegistrationEntry e : entries) {
        	if(e.getIsError())
                gridModel.add(new AutoRegistrationEntryModel(e));
        }
        return gridModel;
    }

    private void createStudentAccounts() {
    	CatchupMathTools.setBusy(true);
        CmServiceAsync s = CmRpcCore.getCmService();
        s.execute(new CreateAutoRegistrationAccountsAction(adminId, student, _preview.getEntries()),
                new AsyncCallback<AutoRegistrationSetup>() {
                    @Override
                    public void onSuccess(AutoRegistrationSetup result) {
                    	EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
                    	
                    	
                    	CatchupMathTools.setBusy(false);

                    	int cnt = result.getEntries().size() - result.getErrorCount();
                    	String msgSuccess = cnt + " bulk student " + (cnt==1?"record":"records") + " created successfully!";

                        if (result.getErrorCount() > 0) {
                        	
                            _buttonClose.setText("Close");
                            //_buttonCreate.setEnabled(false);

                            _previewGrid.getStore().clear();
                            _previewGrid.getStore().addAll(createGxtModelFromEntries(result.getEntries()));
                        	
                            int ok = result.getEntries().size() - result.getErrorCount();
                            
                            String msg = "";
                            if(ok > 0) {
                            	String msgErr = (ok > 1) ? "However, there were errors while creating some of the student accounts."
                            			                 : "However, there was an error while creating one of the student accounts.";
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
                        forceLayout();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    	CatchupMathTools.setBusy(false);
                        caught.printStackTrace();
                        CatchupMathTools.showAlert("Bulk Student Records FAILED: " + caught.getMessage());
                    }

                });
    }

    private ColumnModel<AutoRegistrationEntryModel> defineColumns() {
        
        List<ColumnConfig<AutoRegistrationEntryModel, ?>> cols = new ArrayList<ColumnConfig<AutoRegistrationEntryModel,?>>();

        cols.add(new ColumnConfig<AutoRegistrationEntryModel, String>(__propsAutoReg.name(),125, "Student Name"));
        //date.setSortable(true);
        //date.setMenuDisabled(true);

        
        cols.add(new ColumnConfig<AutoRegistrationEntryModel, String>(__propsAutoReg.password(),125, "Password"));
        //program.setSortable(true);
        //program.setMenuDisabled(true);

        cols.add(new ColumnConfig<AutoRegistrationEntryModel, String>(__propsAutoReg.message(),275, "Message"));
        //message.setSortable(true);
        //message.setMenuDisabled(true);

        return new ColumnModel<AutoRegistrationEntryModel>(cols);
    }
}

class AutoRegistrationEntryModel {

    String name, password, message;
    
    public AutoRegistrationEntryModel(AutoRegistrationEntry entry) {
        this.name = entry.getName();
        this.password = entry.getPassword();
        if (entry.getIsError() == null)
            this.message = "Pending";
        else
            this.message = entry.getMessage();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


interface AutoRegistrationEntryModelProperties extends PropertyAccess<String> {
    @Path("name")
    ModelKeyProvider<AutoRegistrationEntryModel> id();

    ValueProvider<AutoRegistrationEntryModel, String> message();

    ValueProvider<AutoRegistrationEntryModel, String> password();

    ValueProvider<AutoRegistrationEntryModel, String> name();
    
}