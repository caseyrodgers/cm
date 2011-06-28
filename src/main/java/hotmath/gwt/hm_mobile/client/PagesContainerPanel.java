package hotmath.gwt.hm_mobile.client;


import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;

import com.google.gwt.user.client.ui.Widget;

public interface PagesContainerPanel {
	void addPage(IPage p);
	void removePage(IPage p);
	Widget getPanel();
    void onDomTransitionEnded();
    void bind(ObservableStack<IPage> pageModel);
}
