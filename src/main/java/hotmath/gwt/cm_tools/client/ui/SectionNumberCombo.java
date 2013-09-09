package hotmath.gwt.cm_tools.client.ui;


import hotmath.gwt.cm_tools.client.model.SectionNumber;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class SectionNumberCombo extends ComboBox<SectionNumber> {
	
	public static final int DEFAULT_IDX = 0;
	
	static SectionNumberProperties __propsSectNum = GWT.create(SectionNumberProperties.class);

    public SectionNumberCombo(int sectionCount) {
        
        super(new ComboBoxCell<SectionNumber>(new ListStore<SectionNumber>(__propsSectNum.id()), __propsSectNum.sectionLabel()));

        ListStore<SectionNumber> sectionStore = getStore();
        sectionStore.addAll(getSectionNumberList(sectionCount));

        setValue(sectionStore.get(2));
        //setFieldLabel("Select Section");
        setForceSelection(false);
        //setDisplayField("section-number");
        setEditable(false);
        //setMaxLength(30);
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

    public void updateList(int count) {
        getStore().clear();
        getStore().addAll(getSectionNumberList(count));
    }
    
    private List<SectionNumber> getSectionNumberList(int count) {
        List<SectionNumber> list = new ArrayList<SectionNumber>();
        for (int i = 1; i <= count; i++) {
        	list.add(new SectionNumber(i));
        }
        return list;
    }
    
    public int getSectionNumber() {
        return getValue().getSectionNumber();
    }
}

interface SectionNumberProperties extends PropertyAccess<String> {
    @Path("sectionNumber")
    ModelKeyProvider<SectionNumber> id();
    LabelProvider<SectionNumber> sectionLabel();
}
