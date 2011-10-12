package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Event;

/** Provides wrapper around editable row that can be 
 * clicked and edited.
 * 
 * @author casey
 *
 */
class StepUnitWrapper extends LayoutContainer {
    StepUnitItem item;
    public StepUnitWrapper(String title, final SolutionMeta meta, StepUnitItem w) {
        this.item = w;

        setStyleAttribute("position", "relative");
        Button edit = new Button("!");
        edit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new StepEditorPlainTextDialog(item);
            }
        });
        edit.setStyleAttribute("position", "absolute");
        edit.setStyleAttribute("left", "3px");
        edit.setStyleAttribute("top", "-5px");
        edit.setToolTip("Edit");
        add(edit);

        add(w.getWidget());
        addListener(Events.OnDoubleClick, new Listener<BaseEvent>(){
            
            @Override
            public void handleEvent(BaseEvent be) {
                if (be.getType().equals(Events.OnDoubleClick)) {
                    if( ((BoxComponentEvent)be).isControlKey()) {
                        new StepEditorRtfDialog(item);
                    }
                    else {
                        new StepEditorPlainTextDialog(item);
                    }

                }
            } 
         });
        sinkEvents(Event.ONDBLCLICK);        
    }
    
    public StepUnitItem getItem() {
        return item;
    }
    
    
    
}