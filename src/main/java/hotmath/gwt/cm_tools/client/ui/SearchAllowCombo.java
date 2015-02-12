package hotmath.gwt.cm_tools.client.ui;


import hotmath.gwt.cm_core.client.model.SearchAllowMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.ComboBox;


public class SearchAllowCombo extends ComboBox<SearchAllowMode> {
    
    public static final int DEFAULT_PERCENT_IDX = 1;
    static private SearchAllowProperties __props = GWT.create(SearchAllowProperties.class);

    public SearchAllowCombo() {
        
        super(new ComboBoxCell<SearchAllowMode>(new ListStore<SearchAllowMode>(__props.level()),__props.desc()));
        getStore().addAll(getAllowTypes());
        //setValue(getStore().get(2));
        setForceSelection(false);
        
        
        // setFieldLabel("Pass Percent");
        // setDisplayField("pass-percent");
        // setMaxLength(30);
        
        setEditable(false);
        
        setAllowBlank(false);
        setTriggerAction(TriggerAction.ALL);
        setTitle("Select a percentage");
        setId("pass-combo");
        setTypeAhead(true);
        setSelectOnFocus(true);
        setEmptyText("-- select a value --");
        setWidth(100);
        
        setForceSelection(true);
    }

    private List<SearchAllowMode> getAllowTypes() {
        List<SearchAllowMode> list = new ArrayList<SearchAllowMode>();
        list.addAll(Arrays.asList(SearchAllowMode.values()));
        return list;
    }

    /** get search mode */
    public SearchAllowMode getSearchMode() {
        try {
            if(getValue() != null) {
                return SearchAllowMode.values()[getValue().getLevel()];
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return SearchAllowMode.ENABLED_EXCEPT_TESTS;
    }

    interface SearchAllowProperties extends PropertyAccess<String> {
        ModelKeyProvider<SearchAllowMode> level();
        LabelProvider<SearchAllowMode> desc();
    }

}


