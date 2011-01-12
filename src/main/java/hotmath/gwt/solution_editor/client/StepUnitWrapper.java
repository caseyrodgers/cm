package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Event;

/** Provides wrapper around editable row that can be c
 * clicked and edited.
 * 
 * @author casey
 *
 */
class StepUnitWrapper extends LayoutContainer {
    StepUnitItem item;
    public StepUnitWrapper(String title, StepUnitItem w) {
        this.item = w;
        add(w.getWidget());
        addListener(Events.OnDoubleClick, new Listener<BaseEvent>(){
            
            @Override
            public void handleEvent(BaseEvent be) {
                if (be.getType().equals(Events.OnDoubleClick)) {
                    new StepEditorDialog(item);
                }
            } 
         });
        sinkEvents(Event.ONDBLCLICK);        
    }
    
    public StepUnitItem getItem() {
        return item;
    }
    
    
    
}