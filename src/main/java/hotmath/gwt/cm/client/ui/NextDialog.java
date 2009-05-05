package hotmath.gwt.cm.client.ui;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class NextDialog extends Dialog {

	static NextDialog _lastDialog;
	
	public NextDialog(CmContext context) {
		
		_lastDialog = this;
		
		
		
		setStyleName("next-dialog");
		setModal(true);
		setSize(350,170);
		setHeading("What To Do Next");
		setHideOnButtonClick(true);
		setAnimCollapse(true);
		setData("context", context);
			
		setButtons(Dialog.CANCEL);
		setCloseAction(CloseAction.CLOSE);
			
		LayoutContainer lc = new LayoutContainer();
		lc.setStyleName("next-dialog-panel");
		add(lc);		
			
	}
	
	/** Remove the currently (if any) displayed Next Dialog
	 * 
	 */
	static public void destroyCurrentDialog() {
		if(_lastDialog != null) {
			_lastDialog.close();
			_lastDialog = null;
		}
	}
}
