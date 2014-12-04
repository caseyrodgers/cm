package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;

import com.google.code.gwt.storage.client.Storage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.info.Info;

public class GradeLevelChooser extends GWindow {
    
    private static final String GRADE_LEVEL_KEY = "explore_grade_level";
    private static final Integer MIN_GRADE_LEVEL = 8;
    private static final Integer MAX_GRADE_LEVEL = 12;
    private Topic _topic;

    interface Props extends PropertyAccess<String> {
        ModelKeyProvider<GradeLevel> level();
        LabelProvider<GradeLevel> name();
    }
    
    Props props = GWT.create(Props.class);
    private ComboBox<GradeLevel> _combo;
    public GradeLevelChooser(Topic topic) {
        super(false);
        
        this._topic = topic;
        setHeadingText("Grade Level Chooser");
        setPixelSize(400,  180);
        
        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        
        ListStore<GradeLevel> store = new ListStore<GradeLevel>(props.level());
        store.add(new GradeLevel(8, "8th Grade"));
        store.add(new GradeLevel(9, "9th Grade"));
        store.add(new GradeLevel(10, "10th Grade"));
        store.add(new GradeLevel(11, "11th Grade"));
        store.add(new GradeLevel(12, "12th Grade"));
        
        _combo = new ComboBox<GradeLevel>(store,props.name());
        _combo.setAllowBlank(false);
        _combo.setTriggerAction(TriggerAction.ALL);
        
        try {
            _combo.setValue( store.get(getInitialGradeLevel() - MIN_GRADE_LEVEL) );
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        String message = "<h1>Choose the grade level used to select the topic resources</h1>";
        flow.add(new HTML(message));
        
        flow.add(new FieldLabel(_combo, "Grade Level"));
        
        frame.setWidget(flow);
        
        setWidget(frame);
        

        addButton(new TextButton("Explore", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                int gradeLevel = _combo.getCurrentValue().getLevel();
                saveGradeLevel(gradeLevel);
                doExplore(_topic, 1);
            }
        }));
        addCloseButton();
        
        setVisible(true);
    }
    
    private Integer getInitialGradeLevel() {
        Storage ls = Storage.getLocalStorage();
        if(ls != null) {
            String sGl = ls.getItem(GRADE_LEVEL_KEY);
            if(sGl != null) {
                try {
                    return Integer.parseInt(sGl);
                }
                catch(Exception e) { 
                    e.printStackTrace();
                }
            }
        }
        
        return MIN_GRADE_LEVEL;
    }

    protected void saveGradeLevel(int gradeLevel) {
        Storage ls = Storage.getLocalStorage();
        if(ls != null) {
            ls.setItem(GRADE_LEVEL_KEY, Integer.toString(gradeLevel));
        }
    }

    public void doExplore(Topic topic, int level) {
        Info.display("Not Implemented",  "Need to implement in child");
    }

    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                new GradeLevelChooser(new Topic());
            }
        });
    }
    
    
    class GradeLevel {
        int level;
        String name;
        
        public GradeLevel() {}
        public GradeLevel(int level, String name) {
            this.level = level;
            this.name = name;
        }
        public int getLevel() {
            return level;
        }
        public void setLevel(int level) {
            this.level = level;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

}
