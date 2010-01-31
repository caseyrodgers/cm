package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;

/* Provide standard display of student lists
 * 
 */
public class TrendingDataStudentListDialog extends CmWindow {

    public TrendingDataStudentListDialog(String title, List<StudentModelExt> students) {

        setSize(300, 400);
        setHeading(title);
        
        addStyleName("trending-data-student-list");
        ListView<StudentModelExt> view = new ListView<StudentModelExt>() {
            @Override
            protected StudentModelExt prepareData(StudentModelExt model) {
                String s = model.get("name");
                model.set("shortName", Format.ellipse(s, 15));
                return model;
            }
        };

        ListStore<StudentModelExt> store = new ListStore<StudentModelExt>();
        store.add(students);

        view.setTemplate(getTemplate());
        view.setStore(store);
        view.setItemSelector("div.thumb-wrap");
        view.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<BeanModel>>() {

            public void handleEvent(SelectionChangedEvent<BeanModel> be) {
                setHeading("Simple ListView (" + be.getSelection().size() + " items selected)");
            }

        });
        add(view);
        setScrollMode(Scroll.AUTO);

        addButton(new Button("Close", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        }));

        setModal(true);
        setVisible(true);
    }

    private native String getTemplate() /*-{ 
                                        return ['<tpl for=".">',
                                        '<div class="trending-data-student-list-item">{name}</div>', 
                                        '</tpl>'].join(""); 
                                        }-*/;
}
