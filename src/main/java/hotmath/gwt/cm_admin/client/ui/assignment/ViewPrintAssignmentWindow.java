package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.Frame;

public class ViewPrintAssignmentWindow extends GWindow {

	public ViewPrintAssignmentWindow(Assignment assignment) {
		super(true);
		loadAssignment(assignment);
	}

	private void loadAssignment(final Assignment assignment) {
        setPixelSize(640,500);

        setModal(true);
        setResizable(false);
        setHeadingText("Assignment with " + assignment.getProblemCount() + " problems");

        String resource = "/assets/util/assignment.jsp?key=" + assignment.getAssignKey();
        Frame frame = new Frame(resource);
        frame.setSize("100%", "450px");

        add(frame);

        setVisible(true);
	}

}
