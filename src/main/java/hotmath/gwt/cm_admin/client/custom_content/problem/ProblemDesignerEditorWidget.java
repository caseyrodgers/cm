package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.CmGwtUtils;
import hotmath.gwt.cm_core.client.JSOModel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveCustomProblemAction.SaveType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
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

	private HTML _aboutWidgetLabel = new HTML();
    public ProblemDesignerEditorWidget(SolutionInfo _solutionInfo, String widgetJson, CallbackOnComplete callback) {
        super(false);
        setPixelSize(390, 350);
        setResizable(true);
        this._solutionInfo = _solutionInfo;
        this._callback = callback;

        setHeadingText("Input Editor");
        
        _main = new BorderLayoutContainer();
        
        _comboType = createTypeCombo();
        _comboType.addSelectionHandler(new SelectionHandler<ProblemDesignerEditorWidget.WidgetType>() {
            
            @Override
            public void onSelection(SelectionEvent<WidgetType> event) {
                setWidgetEditor(_comboType.getCurrentValue().getWidgetBaseJson());
            }
        });
        
        BorderLayoutData bData = new BorderLayoutData(100);
        bData.setMargins(new Margins(10,10,10,10));
        
        
        FlowPanel headerPanel = new FlowPanel();
        headerPanel.add(new MyFieldLabel(_comboType,"Input Type", 100, 180));
        headerPanel.add(_aboutWidgetLabel);
        
        _main.setNorthWidget(headerPanel, bData);
        
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

            _aboutWidgetLabel.setHTML("<div style='font-style: italic'>" + _widgetEditor.getDescription() + "</div>");
            
            FlowLayoutContainer flow = new FlowLayoutContainer();
            flow.setScrollMode(ScrollMode.AUTO);

            flow.add(_widgetEditor.asWidget());
            _innerFrame.setWidget(flow);
            
            forceLayout();
        } catch (Exception e) {
            Log.error("Error creating input", e);
            Window.alert("Could not create input editor: " + e);
        }
    }
 
	private void doSave() {
        String message=_widgetEditor.checkValid();
        if(message != null) {
            CmMessageBox.showAlert(message);
            return;
            
        }
        final String widgetJson = _widgetEditor.getWidgetJson();
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                
                SaveCustomProblemAction action = new SaveCustomProblemAction(_solutionInfo.getPid(), SaveType.WIDGET, widgetJson);
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }

            @Override
            public void oncapture(RpcData value) {
                CmBusyManager.setBusy(false);
                

                _callback.isComplete();
                hide();
            }
        }.register();
        
    }

    ComboTypeProps props = GWT.create(ComboTypeProps.class);
    String RATIONAL_OR_MIXED="Rational or Mixed Number";
    private ComboBox<WidgetType> createTypeCombo() {
        ListStore<WidgetType> store = new ListStore<WidgetType>(props.key());

        store.add(new  WidgetType("number_integer", "Integer","{'type':'number_integer'}"));
        store.add(new  WidgetType("number_decimal", "Decimal", "{'type':'number_decimal'}"));
        store.add(new  WidgetType("inequality", "Inequality", "{'type': 'inequality'}"));
        store.add(new  WidgetType("number_simple_fraction", "Fraction","{'type': 'number_simple_fraction'}"));
        store.add(new  WidgetType("mChoice", "Multiple Choice", "{'type': 'mChoice', 'value':'THIS|THAT|1'}"));
        store.add(new  WidgetType("number_rational", "Rational","{'type':'number_rational'}"));
        store.add(new  WidgetType("number_rational_mixed", "Rational or Mixed Number","{'type':'number_rational_mixed'}"));
        store.add(new  WidgetType("coordinates", "Ordered Pair","{'type':'coordinates'}"));
        store.add(new  WidgetType("widget_plot", "Plot","{'type':'widget_plot', 'value':'0|0|-10|-10|10|10|1|1'}"));
        //store.add(new  WidgetType("power_form", "Power Form"));
//        store.add(new  WidgetType("power_form", "Power Form"));
//        store.add(new  WidgetType("scientific_notation", "Scientific Notation"));
//        store.add(new  WidgetType("letter", "Text"));
//        store.add(new  WidgetType("odds", "Odds"));
//        store.add(new  WidgetType("point_slope_form", "Point Slope Form"));
//        store.add(new  WidgetType("inequality_exact", "Inequality, Exact"));

        store.add(new  WidgetType("", "Whiteboard", "{'type': ''}"));

        _comboType = new ComboBox<WidgetType>(store, props.label());
        _comboType.setAllowBlank(false);
        _comboType.setTriggerAction(TriggerAction.ALL);
        return _comboType;
    }

    static public void doTest() {
        new ProblemDesignerEditorWidget(new SolutionInfo("custom_2_131219_set1_1_1",null,null,false), "{type:'number_integer', value:'1/200'}", new CallbackOnComplete() {
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
