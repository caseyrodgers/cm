package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;

import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

class TopicSelector extends ComboBox<Topic> {
	
	ListStore<Topic> _store;
	public TopicSelector() {
		super();
		setStyleName("topic-selector");
	    _store = new ListStore<Topic>();
	    _store.add(new Topic("-- All Topics --"));
	    
	    setStore(_store);
		setName("topic");
		setEditable(false);
		
		
		addListener(Events.Select,new Listener() {
			public void handleEvent(BaseEvent be) {
				String selected = getValue().getTopic();
				CatchupMathTools.showAlert("Goto Prescription Topic: " + selected);
			}
		});
	}
	
	public void setSelectedItem(String item) {
		// which index?
	    CatchupMathTools.showAlert("setSelectedItem: " + item);
//		List<String> topics = CatchupMath.getThisInstance().getSessionData().getSessionTopics();
//		for(int i=0;i<topics.size();i++) {
//
//			if(topics.get(i).equals(item)) {
//				setValue(_store.getAt(i));
//				break;
//			}
//		}
	}
	
	public void setAllTopics(List<String> topics) {
		_store.removeAll();
		for(String t: topics) {
			_store.add(new Topic(t));
		}
	}
	
	
	protected void beforeRender() {
	    super.beforeRender();
	    setValue(getStore().getAt(0));
	  }
}

