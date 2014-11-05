package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.JSOModel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.solution_editor.client.list.ComboWidgetWidgetModel;
import hotmath.gwt.solution_editor.client.list.ListSolutionResource;
import hotmath.gwt.solution_editor.client.list.ListWidgetModel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;


public class WidgetListDialog extends GWindow {
    ListSolutionResource  listView = new ListSolutionResource();
    Callback _callback;
    
    TabPanel _tabPanel = new TabPanel();
    TabItemConfig _tabLocal, _tabGlobal;
    ListWidgetModel _listView = new ListWidgetModel();
    TextButton createButton;
    public WidgetListDialog() {
        super(false);
        
        setPixelSize(400,250);
        setModal(true);
        setResizable(false);
        
        setHeadingText("Create Solution Widget");

        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                _callback.resourceSelected(null);
            }
        });
        
        createButton = new TextButton("Create", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                WidgetDefModel widget = getWidgetDef();
                _callback.resourceSelected(widget);
                
                hide();
            }
        });
        addButton(createButton);
        addButton(new TextButton("Close", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        setWidget(createForm());
        setVisible(true);
    }
    
    /** extract data from form and put in 
     * WidgetDefModel
     * 
     * @return
     */
    private WidgetDefModel getWidgetDef() {
        WidgetDefModel widget = new WidgetDefModel();
        widget.setType(_typeCombo.getValue().getType());
        widget.setValue(_inputValue.getValue());
        widget.setFormat(_format.getValue());
        
        widget.setWidth(_width.getValue().intValue());
        widget.setHeight(_height.getValue().intValue());
        return widget;
    }
    
    public void setCallback(Callback callback) {
        this._callback = callback;
    }
    
    /** {"file":"/util/input_widgets/input_comp.swf", 
     *    "id":"genericalg1_2_7_GraphingLinearEquations_1_130", 
     *    "value":"-3|1|4", 
     *    "type":"point_slope_form", 
     *    "width":"375", 
     *    "height":"100"}
     * 
     * @return
     */

    private Widget createForm() {
        FramedPanel frameMain = new FramedPanel();
        frameMain.setHeaderVisible(false);
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        frameMain.setWidget(flow);

        List<WidgetModel> widgets = createListOfWidgets();
        _listView.getStore().addAll(widgets);  
        
        _typeCombo.setEditable(false);
        flow.add(new MyFieldLabel(_typeCombo, "Widget Type", 100, 240));
        _typeCombo.getStore().addAll(widgets);
        
        _inputValue.setAllowBlank(false);  
        _inputValue.getFocusSupport().setPreviousId(frameMain.getButtonBar().getId());  
        flow.add(new MyFieldLabel(_inputValue, "Correct Value", 100, 240));
        

        _format.setAllowBlank(true);
        _format.getFocusSupport().setPreviousId(frameMain.getButtonBar().getId());
        
        List<WidgetModel> formats = createListOfFormats();
        flow.add(new MyFieldLabel(_format, "Format", 100, 240));
        
        //_width.setFieldLabel("Width");
        _width.setAllowBlank(true);
        // _width.addValidator(new VTypeValidator(VType.NUMERIC));
        _width.getFocusSupport().setPreviousId(frameMain.getButtonBar().getId());  
        flow.add(new MyFieldLabel(_width, "Width", 100, 50));
        _height.setAllowBlank(true);  
        _height.getFocusSupport().setPreviousId(frameMain.getButtonBar().getId());  
        flow.add(new MyFieldLabel(_height, "Height", 100, 50));
        
        
        // FormButtonBinding binding = new FormButtonBinding(simple);  
        // binding.addButton(createButton);         
        
        // vp.add(simple);
        
        return frameMain;
    }
    
    public Integer getInt(String o) {
        try {
            return Integer.parseInt(o);
        }
        catch(Exception e) {
            return 0;
        }
    }


    
    
    private void loadForm(WidgetDefModel widgetDef) {
        _typeCombo.setValue(_typeCombo.findModelByType(widgetDef.getType()));
        _inputValue.setValue(widgetDef.getValue());
        
        _format.setValue(widgetDef.getFormat());
        _width.setValue(widgetDef.getWidth());
        _height.setValue(widgetDef.getHeight());   
    }
    
    
    private void setupForm(String widgetJson) {
        if(widgetJson == null || widgetJson.length() == 0) {
            createButton.setText("Create");
        }
        else {
            createButton.setText("Update");
            
            JSOModel model = JSOModel.fromJson(widgetJson);
            WidgetDefModel widgetDef = new WidgetDefModel(model);
            loadForm(widgetDef);
            forceLayout();
        }

    }
    
    private List<WidgetModel> createListOfWidgets() {
        List<WidgetModel> widgets = new ArrayList<WidgetModel>();
        
        widgets.add(new  WidgetModel("number_integer"));
        widgets.add(new  WidgetModel("number_decimal"));
        widgets.add(new  WidgetModel("inequality"));
        widgets.add(new  WidgetModel("number_fraction"));
        widgets.add(new  WidgetModel("number_rational"));
        widgets.add(new  WidgetModel("mChoice"));
        widgets.add(new  WidgetModel("coordinates"));
        widgets.add(new  WidgetModel("number_mixed_fraction"));
        widgets.add(new  WidgetModel("power_form"));
        widgets.add(new  WidgetModel("scientific_notation"));
        widgets.add(new  WidgetModel("letter"));
        widgets.add(new  WidgetModel("odds"));
        widgets.add(new  WidgetModel("point_slope_form"));
        widgets.add(new  WidgetModel("inequality_exact"));
        
        return widgets;
    }
    
    private List<WidgetModel> createListOfFormats() {
        List<WidgetModel> widgets = new ArrayList<WidgetModel>();
        
        widgets.add(new  WidgetModel("money"));
        widgets.add(new  WidgetModel("angle_deg"));
        widgets.add(new  WidgetModel("text_simplified"));
        
        return widgets;
    }
    
    
    ComboWidgetWidgetModel _typeCombo = new ComboWidgetWidgetModel();
    IntegerField _width = new IntegerField();
    TextField _inputValue = new TextField();
    TextField _format = new TextField();     
    IntegerField _height = new IntegerField();
    
    
    

    static public interface Callback {
        void resourceSelected(WidgetDefModel widget);
    }
    
    static private WidgetListDialog __instance;
    static void showWidgetListDialog(Callback callback, String widgetJson) {
        if(true || __instance == null) {
            __instance = new WidgetListDialog();
        }
        __instance.setCallback(callback);
        __instance.setupForm(widgetJson);
        __instance.setVisible(true);
    }
    static void showWidgetListDialog(Callback callback) {
        showWidgetListDialog(callback,null);
    }

    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                new WidgetListDialog();
            }
        });
    }
}

 enum VType {
    ALPHABET("^[a-zA-Z_]+$", "Alphabet"), 
    ALPHANUMERIC("^[a-zA-Z0-9_]+$", "Alphanumeric"), 
    NUMERIC("^[+0-9]+$", "Numeric"),
    EMAIL("^(\\w+)([-+.][\\w]+)*@(\\w[-\\w]*\\.){1,5}([A-Za-z]){2,4}$", "Email");
    String regex;
    String name;

    VType(String regex, String name) {
      this.regex = regex;
      this.name = name;
    }
  }

class VTypeValidator implements Validator<String> {

    private VType type;
    
    public VTypeValidator(VType type){
      this.type = type;
    }

    @Override
    public List validate(Editor editor, String value) {
        String res = null;
        ArrayList<DefaultEditorError> errors = new ArrayList<DefaultEditorError>();
        errors.add(new DefaultEditorError(editor, value + " isn't a valid " + type.name, value));
        if(!value.matches(type.regex)){
          res = value + " isn't a valid " + type.name;
        }
        return errors;        
    }

}