package hotmath.gwt.cm_tools.client.ui;

// import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationAccountsAction;
import hotmath.gwt.shared.client.rpc.action.CreateAutoRegistrationPreviewAction;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationEntry;
import hotmath.gwt.shared.client.rpc.result.AutoRegistrationSetup;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
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
 * Also, provides hookds to upload external configuration file
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
        setModal(true);

        drawGui();
        createPreview();
        setVisible(true);
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
        getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
        addButton(_buttonCancel);

    }

    private FormData formData = new FormData("-20");
    Grid<AutoRegistrationEntryGxt> _previewGrid;

    private void createForm(List<AutoRegistrationEntryGxt> gridModel) {
        removeAll();

        setLayout(new BorderLayout());

        add(createInfoPanel(), new BorderLayoutData(LayoutRegion.NORTH, 85));

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

    private Widget createInfoPanel() {
        String html = " <div class='detail-info' style='height: 55px;' >"
                + "     <div class='form left'>"
                + "        <div class='fld'><label>Program:</label><div>"
                + student.getProgramDescr()
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
                + "<h2 style='margin-left:50px;'>The students listed below will be created using the values shown above.</h2>";
        return new Html(html);
    }

    private void createPreview() {
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new CreateAutoRegistrationPreviewAction(student, uploadFileKey),
                new AsyncCallback<AutoRegistrationSetup>() {
                    @Override
                    public void onSuccess(AutoRegistrationSetup result) {

                        _preview = result;

                        // transfer from local model into Gxt model
                        createForm(createGxtModelFromEntries(result.getEntries()));
                        _buttonCreate.setEnabled(true);
                        CatchupMathTools.setBusy(false);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
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
            gridModel.add(new AutoRegistrationEntryGxt(e));
        }
        return gridModel;
    }

    private void createStudentAccounts() {
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new CreateAutoRegistrationAccountsAction(adminId, student, _preview.getEntries()),
                new AsyncCallback<AutoRegistrationSetup>() {
                    @Override
                    public void onSuccess(AutoRegistrationSetup result) {

                        _buttonCancel.setText("Close");
                        _buttonCreate.setEnabled(false);

                        _previewGrid.getStore().removeAll();
                        _previewGrid.getStore().add(createGxtModelFromEntries(result.getEntries()));

                        EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_REFRESH_STUDENT_DATA));

                        if (result.getErrorCount() > 0) {
                            CatchupMathTools
                                    .showAlert("There were errors while creating the new student accounts.  Please see associated error messages");
                        } else {
                            CatchupMathTools.showAlert("Bulk Student Records created successfully!",
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
