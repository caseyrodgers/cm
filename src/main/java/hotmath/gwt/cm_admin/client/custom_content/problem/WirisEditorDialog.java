package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;

public class WirisEditorDialog extends GWindow {
	
	public WirisEditorDialog(String source) {
		super(true);
		setPixelSize(600,  450);
		Frame frame = new Frame(source);
		frame.setSize("100%",  "400px");
		setWidget(frame);
		setVisible(true);
	}

}
