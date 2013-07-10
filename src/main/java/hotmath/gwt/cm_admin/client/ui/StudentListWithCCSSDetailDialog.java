package hotmath.gwt.cm_admin.client.ui;

import com.google.gwt.user.client.ui.HTML;

public class StudentListWithCCSSDetailDialog extends StudentListDialog {

	public StudentListWithCCSSDetailDialog(String title, int height) {
		super(title, height);
	}

    public void addCCSSDetail(String standardDescription) {
        HTML details = new HTML(standardDescription);
        _container.insert(details, 0);
    }

}
