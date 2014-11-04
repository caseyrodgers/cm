package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.ui.context.CmContext;

import com.google.gwt.user.client.ui.SimplePanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;


class NextDialog extends Dialog {

	static NextDialog _lastDialog;
	
	public NextDialog(CmContext context) {
		
		_lastDialog = this;
		
		setStyleName("next-dialog");
		setModal(true);
		setPixelSize(350,170);
		setHeadingText("What To Do Next");
		setHideOnButtonClick(true);
		setAnimCollapse(true);
		setData("context", context);
			
		addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
			
		SimplePanel sp = new SimplePanel();
		sp.setStyleName("next-dialog-panel");
		setWidget(sp);		
	}
	
	/** Remove the currently (if any) displayed Next Dialog
	 * 
	 */
	static public void destroyCurrentDialog() {
		if(_lastDialog != null) {
			_lastDialog = null;
		}
	}
}
