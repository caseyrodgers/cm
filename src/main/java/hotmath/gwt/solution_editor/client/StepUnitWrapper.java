package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_core.client.CmEvent;
import hotmath.gwt.cm_core.client.EventBus;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.solution_editor.client.StepEditorPlainTextDialog.EditCallback;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Provides wrapper around editable row that can be 
 * clicked and edited.
 * 
 * @author casey
 *
 */
class StepUnitWrapper extends FlowLayoutContainer {
    StepUnitItem item;
    public StepUnitWrapper(String title, final SolutionMeta meta, StepUnitItem w) {
        this.item = w;

        getElement().getStyle().setPosition(Position.RELATIVE);
        TextButton edit = new TextButton("!");
        edit.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new StepEditorPlainTextDialog(new EditCallback() {
					
					@Override
					public void saveTextToEdit(String editedText) {
		        		item.setEditorText(editedText);
		        		EventBus.getInstance().fireEvent(new CmEvent(EventTypes.POST_SOLUTION_LOAD));
		        		EventBus.getInstance().fireEvent(
		        				new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));						
					}
					
					@Override
					public String getTextToEdit() {
						return item.getEditorText();
					}
				});
            }
        });
        edit.getElement().setAttribute("style",  "position: absolute;left: 3px;top: -5px");
        edit.setToolTip("Edit");
        add(edit);

        FocusPanel focusPanel = new FocusPanel();
        focusPanel.add(w.getWidget());
        add(focusPanel);
        focusPanel.addDoubleClickHandler(new DoubleClickHandler() {
            
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                    if( event.isControlKeyDown()) {
                        new StepEditorRtfDialog(item);
                    }
                    else {
                        new StepEditorPlainTextDialog(new StepEditorPlainTextDialog.EditCallback() {
							
							@Override
							public void saveTextToEdit(String editedText) {
				        		item.setEditorText(editedText);
				        		EventBus.getInstance().fireEvent(new CmEvent(EventTypes.POST_SOLUTION_LOAD));
				        		EventBus.getInstance().fireEvent(
				        				new CmEvent(hotmath.gwt.solution_editor.client.EventTypes.SOLUTION_EDITOR_CHANGED));						
							}
							
							@Override
							public String getTextToEdit() {
								return item.getEditorText();								
							}
							
                        }
						);
                    }

                }
         });
    }
    
    public StepUnitItem getItem() {
        return item;
    }
    
    
    
}