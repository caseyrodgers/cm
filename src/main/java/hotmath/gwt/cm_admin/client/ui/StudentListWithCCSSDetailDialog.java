package hotmath.gwt.cm_admin.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class StudentListWithCCSSDetailDialog extends StudentListDialog {

	int detailsHeight;
	
	public StudentListWithCCSSDetailDialog(String title, int height) {
		super(title, height);
		detailsHeight = height;
	}

    public void addCCSSDetail(String standardDescription) {
    	FlowLayoutContainer flc = new FlowLayoutContainer();
    	flc.setScrollMode(ScrollMode.AUTO);
    	flc.setHeight(detailsHeight);
    	flc.setWidth(255);
    	flc.setBorders(true);

        HTML details = new HTML(standardDescription);
    	flc.add(details);
    	BorderLayoutData bld = new BorderLayoutData(90);
    	bld.setMargins(new Margins(5));
        _container.setNorthWidget(flc, bld);
    }

}
