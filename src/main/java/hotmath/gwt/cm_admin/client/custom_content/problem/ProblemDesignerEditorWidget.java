package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.JSOModel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction.SaveType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class ProblemDesignerEditorWidget extends GWindow {

    interface MyUiBinder extends UiBinder<Widget, ProblemDesignerEditorWidget> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    ComboBox<WidgetType> _comboType;
    
    @UiField 
    FramedPanel mainFrame;
    WidgetEditor _widgetEditor;

    private SolutionInfo _solutionInfo;

    private CallbackOnComplete _callback;
    BorderLayoutContainer _main;
    FramedPanel _innerFrame;
    public ProblemDesignerEditorWidget(SolutionInfo _solutionInfo, String widgetJson, CallbackOnComplete callback) {
        super(false);
        setPixelSize(340, 300);
        setMaximizable(true);
        this._solutionInfo = _solutionInfo;
        this._callback = callback;

        setHeadingText("Widget Editor");
        
        _main = new BorderLayoutContainer();
        
        _comboType = createTypeCombo();
        _comboType.addSelectionHandler(new SelectionHandler<ProblemDesignerEditorWidget.WidgetType>() {
            
            @Override
            public void onSelection(SelectionEvent<WidgetType> event) {
                setWidgetEditor(_comboType.getCurrentValue().getWidgetBaseJson());
            }
        });
        
        BorderLayoutData bData = new BorderLayoutData(30);
        bData.setMargins(new Margins(10,10,10,10));
        _main.setNorthWidget(new MyFieldLabel(_comboType,"Widget Type", 100, 100), bData);
        
        _innerFrame = new FramedPanel();
        _innerFrame.setHeaderVisible(false);
        _main.setCenterWidget(_innerFrame);

        setWidgetEditor(widgetJson);
        
        setWidget(uiBinder.createAndBindUi(this));
        
        mainFrame.setHeaderVisible(false);
        mainFrame.setWidget(_main);
        
        addButton(new TextButton("Save", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }
        }));
        addButton(new TextButton("Cancel", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));

        setVisible(true);
    }

    private void setWidgetEditor(String widgetJson) {
        
        JSOModel model = JSOModel.fromJson(widgetJson);
        WidgetDefModel widgetDef = new WidgetDefModel(model);
        
        
        /** Select the correct type in combo
         * 
         */
        for(int i=0, t=_comboType.getStore().getAll().size();i<t;i++) {
            WidgetType type = _comboType.getStore().get(i);
            if(type.getType().equals(widgetDef.getType())) {
                _comboType.setValue(type);
                break;
            }
        }
        
        /** save any shared values 
         * 
         */
        WidgetDefModel wd = _widgetEditor != null?_widgetEditor.getWidgetDef():null;
        
        //vert.add(new MyFieldLabel(_comboType,  "Widget Type", 80, 150));
        try {
            _widgetEditor = WidgetEditorFactory.createEditorFor(widgetDef);
            
            
            /** override any variables already set
             * 
             */
            if(wd != null) {
               _widgetEditor.getWidgetDef().setValue(wd.getValue()) ;
            }

            _innerFrame.setWidget(_widgetEditor.asWidget());
            
            forceLayout();
        } catch (Exception e) {
            Log.error("Error creating widget", e);
            Window.alert("Could not create widget editor: " + e);
        }
    }
    
    private void doSave() {
        final String widgetJson = _widgetEditor.getWidgetJson();
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                SaveCustomProblemAction action = new SaveCustomProblemAction(_solutionInfo.getPid(), SaveType.WIDGET, widgetJson);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(RpcData value) {
                _callback.isComplete();
                hide();
            }
        }.register();
        
    }

    ComboTypeProps props = GWT.create(ComboTypeProps.class);
    private ComboBox<WidgetType> createTypeCombo() {
        ListStore<WidgetType> store = new ListStore<WidgetType>(props.key());

        store.add(new  WidgetType("number_integer", "Integer","{'type':'number_integer'}"));
        store.add(new  WidgetType("number_decimal", "Decimal", "{'type':'number_decimal'}"));
        store.add(new  WidgetType("inequality", "Inequality", "{'type': 'inequality'}"));
        store.add(new  WidgetType("number_fraction", "Fraction","{'type': 'number_fraction'}"));
        store.add(new  WidgetType("mChoice", "Multiple Choice", "{'type': 'mChoice', 'value':'THIS|THAT|1'}"));
        store.add(new  WidgetType("", "No Widget", "{'type': ''}"));
        
//        store.add(new  WidgetType("number_rational", "Rational"));

//        store.add(new  WidgetType("coordinates", "Coordinates"));
//        store.add(new  WidgetType("number_mixed_fraction", "Mixed Fraction"));
//        store.add(new  WidgetType("power_form", "Power Form"));
//        store.add(new  WidgetType("scientific_notation", "Scientific Notation"));
//        store.add(new  WidgetType("letter", "Text"));
//        store.add(new  WidgetType("odds", "Odds"));
//        store.add(new  WidgetType("point_slope_form", "Point Slope Form"));
//        store.add(new  WidgetType("inequality_exact", "Inequality, Exact"));
        
        _comboType = new ComboBox<WidgetType>(store, props.label());
        _comboType.setAllowBlank(false);
        _comboType.setTriggerAction(TriggerAction.ALL);
        return _comboType;
    }

    static public void doTest() {
        new ProblemDesignerEditorWidget(new SolutionInfo("custom_2_131219_set1_1_1",null,null,false), "{type:'number_integer', value:'200'}", new CallbackOnComplete() {
            @Override
            public void isComplete() {
            }
        });
    }
    
    
    class WidgetType {
        String type;
        String label;
        String widgetBaseJson;
        
        public WidgetType(String type, String label, String widgetBaseJson) {
            this.type = type;
            this.label = label;
            this.widgetBaseJson = widgetBaseJson;
        }
        
        public String getType() {
            return type;
        }
        
        public String getLabel() {
            return label;
        }
        
        public String getWidgetBaseJson() {
            return widgetBaseJson;
        }
    }
    
    interface ComboTypeProps extends PropertyAccess<String> {
        @Path("type")
        ModelKeyProvider<WidgetType> key();
        LabelProvider<WidgetType> label();
    }
}
