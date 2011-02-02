package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.JSOModel;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.ui.Widget;


public class WidgetListDialog extends Window {
    ListView<SolutionResourceModel> listView;
    Callback _callback;
    
    TabPanel _tabPanel = new TabPanel();
    TabItem _tabLocal, _tabGlobal;
    ListView<WidgetModel> _listView = new ListView<WidgetModel>();
    Button createButton;
    public WidgetListDialog() {
        setSize(400,280);
        setModal(true);
        setResizable(false);
        
        ListStore<WidgetModel> store = new ListStore<WidgetModel>();
        _listView.setStore(store);
        
        setHeading("Create Solution Widget");
        setScrollMode(Scroll.AUTO);
        
        addWindowListener(new WindowListener() {
            @Override
            public void windowHide(WindowEvent we) {
                _callback.resourceSelected(null);
            }
        });
        
        createButton = new Button("Create", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                
                WidgetDefModel widget = getWidgetDef();
                _callback.resourceSelected(widget);
                
                hide();
            }
        });
        addButton(createButton);
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        add(createForm());
        
        setModal(true);
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
        widget.setFormat(_formatCombo.getValue().getType());
        
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

    FormData formData;
    
    private Widget createForm() {
        
        formData = new FormData("-20");  
        VerticalPanel vp = new VerticalPanel();  
        vp.setSpacing(10);  
        
        FormPanel simple = new FormPanel();
        simple.setHeaderVisible(false);
        simple.setFrame(false);
        simple.setBodyBorder(false);
        simple.setWidth(350);  
        simple.setLabelWidth(100);
        simple.setLabelAlign(LabelAlign.RIGHT);

        List<WidgetModel> widgets = createListOfWidgets();
        ListStore<WidgetModel> store = new ListStore<WidgetModel>();  
        store.add(widgets);  
        
        _typeCombo.setFieldLabel("Widget Type");  
        _typeCombo.setDisplayField("type");  
        _typeCombo.setTriggerAction(TriggerAction.ALL);  
        _typeCombo.setStore(store);  
        _typeCombo.setEditable(false);
        simple.add(_typeCombo, formData);
        
          
        _inputValue.setFieldLabel("Correct Value");
        _inputValue.setAllowBlank(false);  
        _inputValue.getFocusSupport().setPreviousId(simple.getButtonBar().getId());  
        simple.add(_inputValue, formData);
        
        
        _formatCombo.setFieldLabel("Format");
        _formatCombo.setDisplayField("type");
        _formatCombo.setAllowBlank(true);
        _formatCombo.setEditable(true);
        _formatCombo.getFocusSupport().setPreviousId(simple.getButtonBar().getId());
        
        List<WidgetModel> formats = createListOfFormats();
        ListStore<WidgetModel> formatStore = new ListStore<WidgetModel>();  
        formatStore.add(formats);
        _formatCombo.setStore(formatStore);
        _formatCombo.setTriggerAction(TriggerAction.ALL);
        
        simple.add(_formatCombo, formData);
        
        
        _width.setFieldLabel("Width");
        _width.setAllowBlank(true);
        _width.setValidator(new VTypeValidator(VType.NUMERIC));
        _width.getFocusSupport().setPreviousId(simple.getButtonBar().getId());  
        simple.add(_width, formData);
        
        
        _height.setFieldLabel("Height");
        _height.setAllowBlank(true);  
        _height.setValidator(new VTypeValidator(VType.NUMERIC));
        _height.getFocusSupport().setPreviousId(simple.getButtonBar().getId());  
        simple.add(_height, formData);
        
        
        FormButtonBinding binding = new FormButtonBinding(simple);  
        binding.addButton(createButton);         
        
        vp.add(simple);
        
        return vp;
    }
    
    public Integer getInt(String o) {
        try {
            return Integer.parseInt(o);
        }
        catch(Exception e) {
            return 0;
        }
    }
    
    
    /** Extract just the widget JSON 
     * 
     * @param html
     * @return
     */
    static public String extractWidgetJson(String html) {

       String START_TOKEN="hm_flash_widget_def";
       
       int startPos = html.indexOf(START_TOKEN);
       if(startPos == -1) {
           return null;
       }
       
       startPos = html.indexOf("{", startPos);
       int endPos = html.indexOf("}", startPos);
       String json = html.substring(startPos, endPos+1);
       
       return json;
    }
    
    /** Return html without any widget definition 
     * 
     * @param html
     * @return
     */
    static public String stripWidgetFromHtml(String html) {
        String START_TOKEN="<div id='hm_flash_widget'";
        int startPos = html.indexOf(START_TOKEN);
        if(startPos == -1) {
            return html;
        }
        int endPos = html.indexOf("</div>", startPos);
        endPos = html.indexOf("</div>", endPos+1);

        String htmlNew = html.substring(0, startPos);
        htmlNew += html.substring(endPos+6);
        
        return htmlNew;    
    }   


    
    
    private void loadForm(WidgetDefModel widgetDef) {
        _typeCombo.setValue(_typeCombo.getStore().findModel("type",widgetDef.getType()));
        _inputValue.setValue(widgetDef.getValue());
        
        WidgetModel model = _formatCombo.getStore().findModel("type",widgetDef.getFormat());
        _formatCombo.setValue(model);
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
            layout();
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
    
    
    ComboBox<WidgetModel> _typeCombo = new ComboBox<WidgetModel>();
    NumberField _width = new NumberField();
    TextField<String> _inputValue = new TextField<String>();
    ComboBox<WidgetModel> _formatCombo = new ComboBox<WidgetModel>();     
    NumberField _height = new NumberField();
    
    
    

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

class VTypeValidator implements Validator {

    private VType type;
    
    public VTypeValidator(VType type){
      this.type = type;
    }
    @Override
    public String validate(Field<?> field, String value) {
      String res = null;
      if(!value.matches(type.regex)){
        res = value + " isn't a valid " + type.name;
      }
      return res;
    }

}