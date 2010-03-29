package hotmath.gwt.cm_tools.client.ui;


import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class PassPercentCombo extends ComboBox<PassPercent> {
	
	public static final int DEFAULT_PERCENT_IDX = 2;

    public PassPercentCombo(boolean passPercentReqd) {

        ListStore<PassPercent> passStore = new ListStore<PassPercent>();
        passStore.add(getPassPercentList());

        setValue(passStore.getAt(2));
        setFieldLabel("Pass Percent");
        setForceSelection(false);
        setDisplayField("pass-percent");
        setEditable(false);
        setMaxLength(30);
        setAllowBlank(false);
        setTriggerAction(TriggerAction.ALL);
        setStore(passStore);
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
        list.add(new PassPercent("90%"));
        list.add(new PassPercent("100%"));
        return list;
    }
    
    public Integer getPassPercent() {
        String value = getValue().getPassPercent();
        return Integer.parseInt(value.substring(0,value.length()-1));
    }

    
}



