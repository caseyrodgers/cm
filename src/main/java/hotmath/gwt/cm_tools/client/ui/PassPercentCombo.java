package hotmath.gwt.cm_tools.client.ui;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.ComboBox;


public class PassPercentCombo extends ComboBox<PassPercent> {
	
	public static final int DEFAULT_PERCENT_IDX = 1;
	static private PassPercentProperties __propsPassPercent = GWT.create(PassPercentProperties.class);

    public PassPercentCombo(boolean passPercentReqd) {
        
        super(new ComboBoxCell<PassPercent>(new ListStore<PassPercent>(__propsPassPercent.id()),__propsPassPercent.percent()));
        getStore().addAll(getPassPercentList());
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
        
    	if (passPercentReqd) {
    		enable();
    		setForceSelection(true);
    	}
    	else {
    		disable();
    		setForceSelection(false);
    	}
    }

    private List<PassPercent> getPassPercentList() {
        List<PassPercent> list = new ArrayList<PassPercent>();
        list.add(new PassPercent("60%"));
        list.add(new PassPercent("70%"));
        list.add(new PassPercent("80%"));
        //list.add(new PassPercent("90%"));
        //list.add(new PassPercent("100%"));
        return list;
    }
    
    public Integer getPassPercent() {
        String value = getValue().getPercent();
        return Integer.parseInt(value.substring(0,value.length()-1));
    }

    interface PassPercentProperties extends PropertyAccess<String> {
        @Path("percent")
        ModelKeyProvider<PassPercent> id();
        LabelProvider<PassPercent> percent();
    }
}


