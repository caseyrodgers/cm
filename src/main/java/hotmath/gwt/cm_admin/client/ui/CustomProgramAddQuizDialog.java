package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.Widget;

public class CustomProgramAddQuizDialog extends Window {

    Callback callback;
    public CustomProgramAddQuizDialog(Callback callback) {
        this.callback = callback;
        setHeading("Define Custom Quiz");
        setSize(640,480);
        
        
        setLayout(new BorderLayout());
        add(createTopPanel(), new BorderLayoutData(LayoutRegion.NORTH, 40));
        add(createBodyPanel(), new BorderLayoutData(LayoutRegion.CENTER));
        
        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        
        addButton(new Button("Save", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        }));
        setVisible(true);
    }
    
    private Widget createBodyPanel() {
        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new RowLayout(Orientation.HORIZONTAL));
        RowData data = new RowData(.5, 1);
        data.setMargins(new Margins(5));

        
        ContentPanel cpLeft = new ContentPanel();
        cpLeft.setHeading("Available Questions");
        cpLeft.setLayout(new FitLayout());
        
        TabPanel tabPanel = new TabPanel();
        TabItem tabItem = new TabItem("Program Tree");
        tabPanel.add(tabItem);
        
        tabItem = new TabItem("All Lessons");
        tabPanel.add(tabItem);
        cpLeft.add(tabPanel);
        lc.add(cpLeft, data);
        
        
        ContentPanel cpRight = new ContentPanel();
        cpRight.setHeading("Questions in Quiz");
        cpRight.setLayout(new FitLayout());
        lc.add(cpRight, data);
        
        return lc;  
    }
    
    
    private Widget createTopPanel() {
        FormPanel form = new FormPanel();
        form.setFrame(false);
        form.setBodyBorder(false);
        form.setHeaderVisible(false);
        form.setLabelWidth(75);
        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new FitLayout());
        lc.addStyleName("custom-quiz-top-panel");
        ComboBox<QuizNamesModel> quizNames = new ComboBox<QuizNamesModel>();
        ListStore<QuizNamesModel> store = new ListStore<QuizNamesModel>();
        quizNames.setFieldLabel("Quiz Name");
        quizNames.setStore(store);
        form.add(quizNames);
        lc.add(form);
        return lc;
    }
    
    static public interface Callback {
        void quizCreated();
    }
}
