package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.WebLinkAddTargetsDialog.Callback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction.CrudOperation;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class WebLinkEditorDialog extends GWindow {

    private BorderLayoutContainer _main;
    private TextField nameField;
    private ContentPanel _linkTargetPanel;
    private TextField urlField;
    private TextArea commentsField = new TextArea();
    private WebLinkModel webLinkModel;
    private CallbackOnComplete callbackOnComplete;
    private ContentPanel _groupsPanel;
    private ComboBox<AvailableDevice> _availableDevice;

    public WebLinkEditorDialog(WebLinkModel webLinkModel, CallbackOnComplete callbackOnComplete) {
        super(false);
        this.callbackOnComplete = callbackOnComplete;

        this.webLinkModel = webLinkModel;
        setPixelSize(550, 440);
        setMaximizable(true);

        setHeadingText("Web Link Editor: " + webLinkModel.getName());
        _main = new BorderLayoutContainer();
        BorderLayoutData bld = new BorderLayoutData(145);
        // bld.setSplit(true);

        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        nameField = new TextField();
        nameField.setValue(webLinkModel.getName());
        urlField = new TextField();
        urlField.setWidth(400);
        urlField.setValue(webLinkModel.getUrl());

        commentsField.setWidth(400);
        commentsField.setValue(webLinkModel.getComments());

        
        _availableDevice = createAvailableCombo();
        
        AvailableOn av = webLinkModel.getAvailableWhen();
        int ordinal = av.ordinal();
        _availableDevice.setValue(_availableDevice.getStore().get(ordinal));

        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(new MyFieldLabel(nameField, "Web Link Name",100,200));
        flow.add(new FieldLabel(urlField, "URL"));
        flow.add(new FieldLabel(commentsField,"Comments"));
        flow.add(new FieldLabel(_availableDevice, "Available On"));
        
        
        

        frame.setWidget(flow);

        _main.setNorthWidget(frame, bld);

        _linkTargetPanel = new ContentPanel();
        _linkTargetPanel.setWidget(createTargetLessonGrid(webLinkModel));
        _linkTargetPanel.addTool(createAddTargetsButton());
        _linkTargetPanel.addTool(createDeleteTargetButton());
        _linkTargetPanel.getHeader().setText("Lesson(s) Selection");

        // _linkTargetPanel.setToolTip("Web link will be available on these lessons");

        BorderLayoutContainer center = new BorderLayoutContainer();
        bld = new BorderLayoutData(.50);
        bld.setSplit(true);
        center.setWestWidget(_linkTargetPanel, bld);

        _groupsPanel = new ContentPanel();
        _groupsPanel.setWidget(createGroupsGrid(webLinkModel));
        _groupsPanel.addTool(createAddGroupButton());
        _groupsPanel.addTool(createDeleteGroupButton());
        _groupsPanel.getHeader().setText("Group(s) Selection");

        center.setCenterWidget(_groupsPanel);
        _main.setCenterWidget(center);
        setWidget(_main);

        addButton(new TextButton("Save", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                saveWebLink();
            }
        }));
        addCloseButton();

        setEnabledOnOff();

        setVisible(true);
    }

    interface AvailDeviceProps extends PropertyAccess<String> {

        LabelProvider<AvailableDevice> device();
        @Path("id")
        ModelKeyProvider<AvailableDevice> key();
    }
    
    private ComboBox<AvailableDevice> createAvailableCombo() {
        AvailDeviceProps props = GWT.create(AvailDeviceProps.class);
        ListStore<AvailableDevice> store = new ListStore<AvailableDevice>(props.key());
        ComboBox<AvailableDevice> combo = new ComboBox<AvailableDevice>(store, props.device());
        
        combo.getStore().add(new AvailableDevice("Desktop and Mobile",AvailableOn.DESKTOP_AND_MOBILE));
        combo.getStore().add(new AvailableDevice("Desktop Only", AvailableOn.DESKTOP_ONLY));
        combo.getStore().add(new AvailableDevice("Mobile Only", AvailableOn.MOBILE_ONLY));
        
        combo.setAllowBlank(false);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.setToolTip("On what type of devices should this web link be shown?");
        return combo;
    }

    private Widget createDeleteGroupButton() {
        return new TextButton("Delete", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _gridGroups.removeSelectedGroup();
            }
        });
    }

    private Widget createAddGroupButton() {
        return new TextButton("Add", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new WebLinkEditorGroupSelectDialog(webLinkModel, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        _gridGroups.setGroups(webLinkModel.getLinkGroups());
                    }
                });
            }
        });

    }

    WebLinkEditorGroupsGrid _gridGroups;

    private Widget createGroupsGrid(WebLinkModel webLinkModel) {
        _gridGroups = new WebLinkEditorGroupsGrid(webLinkModel);
        return _gridGroups;
    }

    protected void saveWebLink() {
        try {
            validateForm();
        } catch (Exception e) {
            Log.error("error saving web link", e);
            CmMessageBox.showAlert("Validation Problem", "This web link could not be saved: " + e.getMessage());
            return;
        }

        String name = nameField.getValue();
        String url = urlField.getValue();
        
        webLinkModel.setName(name);
        webLinkModel.setUrl(url);
        webLinkModel.setComments(commentsField.getValue());

        webLinkModel.getLinkTargets().clear();
        webLinkModel.getLinkTargets().addAll(_grid4LessonTarget.getStore().getAll());

        webLinkModel.getLinkGroups().clear();
        webLinkModel.getLinkGroups().addAll(_gridGroups._grid4Groups.getStore().getAll());
        
        webLinkModel.setAvailableWhen(_availableDevice.getValue().getAvailWhen());
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                DoWebLinksCrudOperationAction action = new DoWebLinksCrudOperationAction(webLinkModel.getAdminId(), CrudOperation.ADD, webLinkModel);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData data) {
                CmBusyManager.setBusy(false);
                callbackOnComplete.isComplete();
                hide();
            }
        }.attempt();
    }

    private void validateForm() throws Exception {
        String name = nameField.getValue();
        if (name == null || name.length() == 0) {
            throw new Exception("Name must be specified");
        }

        String url = urlField.getValue();
        if (url == null || url.length() == 0) {
            throw new Exception("URL must be specified");
        }
        String s[] = url.split("//");
        if (s.length != 2 || !s[0].startsWith("http")) {
            throw new Exception("URL specified is invalid");
        }
    }

    private Widget createDeleteTargetButton() {
        TextButton btn = new TextButton("Delete", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                LessonModel lm = _grid4LessonTarget.getSelectionModel().getSelectedItem();
                if (lm == null) {
                    CmMessageBox.showAlert("Select a web link first");
                } else {
                    _grid4LessonTarget.getStore().remove(lm);
                    if (_grid4LessonTarget.getStore().size() > 0) {
                        _grid4LessonTarget.getSelectionModel().select(_grid4LessonTarget.getStore().get(0), false);
                    }
                }
                
                checkEmptyTargetGrid();
            }
        });
        return btn;
    }

    private Widget createAddTargetsButton() {
        TextButton btn = new TextButton("Add", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                WebLinkAddTargetsDialog.getSharedInstance(new Callback() {

                    @Override
                    public void targetsAdded(List<LessonModel> targetResults) {

                        if (_grid4LessonTarget.getStore().size() == 1 && _grid4LessonTarget.getStore().get(0).getLessonName().equals("All Lessons")) {
                            _grid4LessonTarget.getStore().clear();
                        }

                        for (LessonModel tr : targetResults) {
                            if (!_grid4LessonTarget.getStore().getAll().contains(tr)) {
                                _grid4LessonTarget.getStore().add(tr);
                            }
                        }
                    }
                });
            }
        });

        return btn;
    }

    protected void setEnabledOnOff() {
        // _linkTargetPanel.setEnabled(!linkAlwaysField.getValue());
    }

    Grid4LessonsProperties props = GWT.create(Grid4LessonsProperties.class);
    Grid<LessonModel> _grid4LessonTarget;

    private IsWidget createTargetLessonGrid(WebLinkModel webLinkModel) {
        List<ColumnConfig<LessonModel, ?>> cols = new ArrayList<ColumnConfig<LessonModel, ?>>();
        cols.add(new ColumnConfig<LessonModel, String>(props.lessonName(), 250, "Lesson Name"));
        ColumnModel<LessonModel> cm = new ColumnModel<LessonModel>(cols);
        ListStore<LessonModel> store = new ListStore<LessonModel>(props.key());
        store.addAll(webLinkModel.getLinkTargets());
        
        _grid4LessonTarget = new Grid<LessonModel>(store, cm);
        _grid4LessonTarget.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        checkEmptyTargetGrid();
        return _grid4LessonTarget;
    }

    private void checkEmptyTargetGrid() {
        if(_grid4LessonTarget.getStore().size() == 0) {
            _grid4LessonTarget.getStore().add(new LessonModel("All Lessons", null));
        } 
    }

    interface Grid4LessonsProperties extends PropertyAccess<String> {
        @Path("lessonMame")
        ModelKeyProvider<LessonModel> key();

        ValueProvider<LessonModel, String> lessonName();
    }

    public static void startTest() {
        WebLinkModel model = new WebLinkModel(1, 2, "New Link", "http://math.org", "The Comment", AvailableOn.DESKTOP_AND_MOBILE);
        new WebLinkEditorDialog(model, new CallbackOnComplete() {
            @Override
            public void isComplete() {
                Window.alert("New Link Save");
            }
        });
    }
    
    class AvailableDevice {
        String device;
        private int id;
        private AvailableOn availableWhen;
        
        public AvailableDevice(String label, AvailableOn available) {
            this.availableWhen = available;
            device = label;
            id = available.ordinal();
        }
        
        public AvailableOn getAvailWhen() {
            // TODO Auto-generated method stub
            return availableWhen;
        }

        public String getDevice() {
            return device;
        }
        
        public int getId() {
            return this.id;
        }
    }
}


