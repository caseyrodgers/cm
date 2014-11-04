package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.ComboBox;

class TopicSelector extends ComboBox<Topic> {

    interface Props extends PropertyAccess<String> {
        ModelKeyProvider<Topic> key();
        LabelProvider<Topic> label();
    }

    static Props props = GWT.create(Props.class);

    public TopicSelector() {
        super(new ListStore<Topic>(props.key()), props.label());
        setStyleName("topic-selector");
        getStore().add(new Topic("-- All Topics --"));

        setName("topic");
        setEditable(false);

        addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                String selected = getValue().getTopic();
                CatchupMathTools.showAlert("Goto Prescription Topic: " + selected);
            }
        });
    }

    public void setSelectedItem(String item) {
        // which index?
        CatchupMathTools.showAlert("setSelectedItem: " + item);
        // List<String> topics =
        // CatchupMath.getThisInstance().getSessionData().getSessionTopics();
        // for(int i=0;i<topics.size();i++) {
        //
        // if(topics.get(i).equals(item)) {
        // setValue(_store.getAt(i));
        // break;
        // }
        // }
    }

    public void setAllTopics(List<String> topics) {
        getStore().clear();
        for (String t : topics) {
            getStore().add(new Topic(t));
        }
    }

}
