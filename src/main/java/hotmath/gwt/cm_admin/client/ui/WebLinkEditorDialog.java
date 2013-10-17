package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.WebLinkAddTargetsDialog.Callback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
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
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class WebLinkEditorDialog extends GWindow {
    

    private BorderLayoutContainer _main;
    private TextField nameField;
    private CheckBox linkAlwaysField;
    private ContentPanel _linkTargetPanel;
    private TextField urlField;
    private WebLinkModel webLinkModel;
    private CallbackOnComplete callbackOnComplete;

    public WebLinkEditorDialog(WebLinkModel webLinkModel, CallbackOnComplete callbackOnComplete) {
        super(false);
        this.callbackOnComplete = callbackOnComplete;
        
        this.webLinkModel = webLinkModel;
        setPixelSize(550, 440);
        setHeadingText("Web Link Editor: " + webLinkModel.getName());
        _main = new BorderLayoutContainer();
        BorderLayoutData bld = new BorderLayoutData(110);
        bld.setSplit(true);
        
        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        nameField = new TextField();
        nameField.setValue(webLinkModel.getName());
         urlField = new TextField();
        urlField.setWidth(350);
        urlField.setValue(webLinkModel.getUrl());
        
        linkAlwaysField = new CheckBox();
        linkAlwaysField.setToolTip("This web link will be available for all lessons");
        linkAlwaysField.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setEnabledOnOff();                
            }
        });
        
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(new FieldLabel(nameField,  "Web Link Name"));
        flow.add(new FieldLabel(urlField,  "URL"));
        flow.add(new MyFieldLabel(linkAlwaysField, "Always Available",100,20));
        
        frame.setWidget(flow);
        
        _main.setNorthWidget(frame, bld);
        
        
        _linkTargetPanel = new ContentPanel();
        _linkTargetPanel.setWidget(createGrid(webLinkModel));
        _linkTargetPanel.addTool(createAddTargetsButton());
        _linkTargetPanel.addTool(createDeleteTargetButton());
        _linkTargetPanel.getHeader().setText("Link Targets");
        
        //_linkTargetPanel.setToolTip("Web link will be available on these lessons");
        
        _main.setCenterWidget(_linkTargetPanel);
        setWidget(_main);
        
        
        addTool(createGroupsButton());
        
        addButton(new TextButton("Save", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                saveWebLink();
            }
        }));
        addCloseButton();
        
        
        if(webLinkModel.getAlwaysAvailable()) {
            linkAlwaysField.setValue(true);
        }
        
        setEnabledOnOff();
        
        setVisible(true);
    }
    private Widget createGroupsButton() {
        TextButton btn = new TextButton("Groups", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new WebLinkEditorGroupSelectDialog(webLinkModel);
            }
        });
        btn.setToolTip("Select which groups this web link is available");
        return btn;
    }


    protected void saveWebLink() {
        try {
            validateForm();
        }
        catch(Exception e) {
            Log.error("error saving web link", e);
            CmMessageBox.showAlert("Validation Problem","This web link could not be saved: " + e.getMessage());
        }
        
        String name = nameField.getValue();
        String url = urlField.getValue();
        boolean always = linkAlwaysField.getValue();
        
        webLinkModel.setName(name);
        webLinkModel.setUrl(url);
        webLinkModel.setAlwaysAvailable(always);
        
        webLinkModel.getLinkTargets().clear();
        webLinkModel.getLinkTargets().addAll(_grid.getStore().getAll());
        
            new RetryAction<RpcData>() {
                @Override
                public void attempt() {
                    CmBusyManager.setBusy(true);
                    DoWebLinksCrudOperationAction action = new DoWebLinksCrudOperationAction(webLinkModel.getAdminId(),CrudOperation.ADD, webLinkModel);
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
        if(name == null || name.length() == 0) {
            throw new Exception("Name must be specified");
        }
        
        String url = urlField.getValue();
        if(url==null || url.length() == 0) {
            throw new Exception("URL must be specified");
        }
        String s[] = url.split("//");
        if(s.length != 2 || !s[0].startsWith("http")) {
            throw new Exception("URL specified is invalid");
        }
    }

    private Widget createDeleteTargetButton() {
        TextButton btn = new TextButton("Remove Link Target", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                LessonModel lm = _grid.getSelectionModel().getSelectedItem();
                if(lm == null) {
                    CmMessageBox.showAlert("Select a web link first");
                }
                else {
                    _grid.getStore().remove(lm);
                    if(_grid.getStore().size() > 0) {
                        _grid.getSelectionModel().select(_grid.getStore().get(0), false);
                    }
                }
            }
        });
        return btn;
    }

    private Widget createAddTargetsButton() {
        TextButton btn = new TextButton("Add Link Target",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                WebLinkAddTargetsDialog.getSharedInstance(new Callback() {
                    
                    @Override
                    public void targetsAdded(List<LessonModel> targetResults) {
                        for(LessonModel tr: targetResults) {
                            if(!_grid.getStore().getAll().contains(tr)) {
                                _grid.getStore().add(tr);
                            }
                        }
                    }
                });
            }
        });
        
        return btn;
    }

    protected void setEnabledOnOff() {
        _linkTargetPanel.setEnabled(!linkAlwaysField.getValue());
    }

    Grid4LessonsProperties props = GWT.create(Grid4LessonsProperties.class);
    Grid<LessonModel> _grid;
    private IsWidget createGrid(WebLinkModel webLinkModel) {
        List<ColumnConfig<LessonModel, ?>> cols = new ArrayList<ColumnConfig<LessonModel, ?>>();
        cols.add(new ColumnConfig<LessonModel, String>(props.subject(), 100, "Subject"));
        cols.add(new ColumnConfig<LessonModel, String>(props.lessonName(), 300, "Lesson Name"));
        ColumnModel<LessonModel> cm = new ColumnModel<LessonModel>(cols);
        ListStore<LessonModel> store = new ListStore<LessonModel> (props.key());
        
        store.addAll(webLinkModel.getLinkTargets());
        
        _grid = new Grid<LessonModel>(store, cm);
        _grid.setEnabled(false);
        return _grid;
    }
    
    interface Grid4LessonsProperties extends PropertyAccess<String> {
        @Path("lessonMame")
        ModelKeyProvider<LessonModel> key();
        ValueProvider<LessonModel, String> subject();
        ValueProvider<LessonModel, String> lessonName();
    }
    
    public static void startTest() {
        WebLinkModel model = new WebLinkModel(1, 2, "New Link",  "http://math.org");
        model.setAlwaysAvailable(true);
        new WebLinkEditorDialog(model, new CallbackOnComplete() {
            
            @Override
            public void isComplete() {
                Window.alert("New Link Save");
            }
        });
    }
}
