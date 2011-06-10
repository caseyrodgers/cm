package hotmath.gwt.cm_tools.client.ui;


import java.util.ArrayList;
import java.util.List;

import hotmath.gwt.cm_tools.client.model.SectionNumber;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class SectionNumberCombo extends ComboBox<SectionNumber> {
	
	public static final int DEFAULT_IDX = 0;

    public SectionNumberCombo(int sectionCount) {

        ListStore<SectionNumber> sectionStore = new ListStore<SectionNumber>();
        sectionStore.add(getSectionNumberList(sectionCount));

        setValue(sectionStore.getAt(2));
        setFieldLabel("Set Section");
        setForceSelection(false);
        setDisplayField("section-number");
        setEditable(false);
        setMaxLength(30);
        setAllowBlank(false);
        setTriggerAction(TriggerAction.ALL);
        setStore(sectionStore);
        setTitle("Select a section");
        setId("section-number");
        setTypeAhead(true);
        setSelectOnFocus(true);
        setEmptyText("-- select a value --");
        setWidth(100);
    }

    private List<SectionNumber> getSectionNumberList(int count) {
        List<SectionNumber> list = new ArrayList<SectionNumber>();
        for (int i = 1; i <= count; i++) {
        	list.add(new SectionNumber(String.valueOf(i)));
        }
        return list;
    }
    
    public Integer getSectionNumber() {
        String value = getValue().getSectionNumber();
        return Integer.parseInt(value);
    }

    
}



;