package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_tools.client.ui.GWindow;

public class AddChoiceEditor extends GWindow {

	interface Callback {
		
	}
	private Object callback;
	public AddChoiceEditor(	Callback callBack) {
		super(true);
		this.callback = callback;
		
		setHeadingHtml("Multiple Choice Editor");
		setResizable(true);
		setModal(true);
	}

}
