package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceWebLinkView;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.event.shared.EventBus;

public class PrescriptionLessonResourceWebLinkActivity  implements PrescriptionLessonResourceWebLinkView.Presenter {
	InmhItemData resourceItem;
    com.google.gwt.event.shared.EventBus  eventBus;
    	
    public PrescriptionLessonResourceWebLinkActivity(EventBus eb, InmhItemData itemData) {
	    this.eventBus = eb;
	    this.resourceItem = itemData;
    }
	
    @Override
    public String getWebLinkName() {
        return resourceItem.getTitle();
    }
    
    @Override
    public String getWebLinkUrl() {
        return resourceItem.getFile();
    }
}