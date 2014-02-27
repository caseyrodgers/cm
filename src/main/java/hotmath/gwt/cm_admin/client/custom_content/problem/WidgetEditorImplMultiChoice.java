package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

public class WidgetEditorImplMultiChoice extends WidgetEditorImplBase {

    public WidgetEditorImplMultiChoice(WidgetDefModel widgetDef) {
        super(widgetDef);
    }
    
    MultiGridProps props = GWT.create(MultiGridProps.class);
    
    boolean areChanges;
    Grid<MultiValue> _grid;
    protected void buildUi() {
        final ListStore<MultiValue> store = new ListStore<MultiValue>(props.key());
        List<ColumnConfig<MultiValue, ?>> cols = new ArrayList<ColumnConfig<MultiValue, ?>>();
        ColumnConfig<MultiValue, String> nameCol = new ColumnConfig<MultiValue, String>(props.value(),200,"Value");
        ColumnConfig<MultiValue, Boolean> correctCol = new ColumnConfig<MultiValue, Boolean>(props.correct(),40,"Correct");
        cols.add(nameCol);
        cols.add(correctCol);
        
        String choiceData = getWidgetDef().getValue();
        String c[] = choiceData.split("\\|");
        int correctIndex = 0;
        if(c.length > 1) {
            correctIndex = Integer.parseInt(c[c.length-1]);
            for(int i=0,t=c.length;i<(t-1);i++) {
                store.add(new MultiValue(c[i], false));
            }
            store.get(correctIndex).setCorrect(true);
        }

        store.setAutoCommit(true);
        
        ColumnModel<MultiValue> colModel = new ColumnModel<MultiValue>(cols);
        _grid = new Grid<MultiValue>(store, colModel);
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _grid.getView().setAutoFill(true);
        

        GridEditing<MultiValue> editor = new GridInlineEditing<MultiValue>(_grid);
        editor.addCompleteEditHandler(new CompleteEditHandler<MultiValue>() {
            @Override
            public void onCompleteEdit(CompleteEditEvent<MultiValue> event) {
                areChanges = true;

                MultiValue mv = _grid.getSelectionModel().getSelectedItem();
                MultiValue ch = store.getAll().get(event.getEditCell().getRow());
                
                /** only one can be selected */
                if(ch.isCorrect()) {
                    for(MultiValue s: store.getAll()) {
                        if(s != ch) {
                            s.setCorrect(false);
                            store.update(s);
                        }
                    }
                }                
            }
        });
        editor.addEditor(nameCol, new TextField());
        editor.addEditor(correctCol, new CheckBox());

        
        ContentPanel content = new ContentPanel();
        content.setWidget(editor.getEditableGrid());
        
        
        content.addTool(new TextButton("Add Choice", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                addChoice();
            }

        }));
        content.addTool(new TextButton("Remove Choice", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                removeSelectedChoice();
            }
        }));
        
        _fields.clear();
        _fields.add(content);
        // initWidget(content);
    }

    @Override
    protected String getWidgetType() {
        return "mChoice";
    }

    protected void removeSelectedChoice() {
        if(selected() != null) {
            _grid.getStore().remove(selected());
        }
    }
    

    private void addChoice() {
        _grid.getStore().add(new MultiValue("Empty Value",false));
    }
    
    private MultiValue selected() {
        if(_grid.getSelectionModel().getSelectedItem() == null) {
            CmMessageBox.showAlert("no value selected");
            return null;
        }
        else {
            return _grid.getSelectionModel().getSelectedItem();
        }
    }


    @Override
    protected WidgetDefModel createWidgetDefModel() {
        WidgetDefModel wd = super.createWidgetDefModel();
        wd.setValue(getValueString());
        return wd;
    }
    
    private String getValueString() {

        String value="";
        List<MultiValue> vals = _grid.getStore().getAll();
        int correctSel=0;
        for(int i=0, t=vals.size();i<t;i++) {
            if(value.length() > 0) {
                value += "|";
            }
            
            MultiValue v = vals.get(i);
            value += v.getValue();
            
            if(v.isCorrect) {
                correctSel = i;
            }
        }
        value += "|" + correctSel;
        return value;
    }

    static class MultiValue {
        static int __key;
        int key;
        String value;
        private boolean isCorrect;
        
        public MultiValue(String value, boolean isCorrect) {
            this.value = value;
            this.isCorrect = isCorrect;
            this.key = __key++;
        }
        
        public int getKey() {
            return key;
        }
        
        public String getValue() {
            return value;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
    }
    
    interface MultiGridProps extends PropertyAccess<String> {
        ModelKeyProvider<MultiValue> key();
        ValueProvider<MultiValue, Boolean> correct();
        ValueProvider<MultiValue, String> value();
    }
}
