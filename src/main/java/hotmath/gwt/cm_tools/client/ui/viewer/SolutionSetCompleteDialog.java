package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

public class SolutionSetCompleteDialog extends GWindow {

    public SolutionSetCompleteDialog(int numCorrect, int limit) {
        super(false);

        setHeadingText("Problem Set Results");

        setModal(true);
        setPixelSize(350, 150);
        addStyleName("SolutionSetCompleteDialog");
        String html = "You correctly answered <b style='font-size: 1.5em'> " + numCorrect
                + "</b> questions out of <b style='font-size: 1.5em'>" + limit + "</b>.";

        html = "<p style='text-align: center;margin-top: 10px;' class='solution_set_results'>" + html + "</p>";

        setWidget(new HTML(html));

        addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
            }
        });

        addCloseButton();
        setVisible(true);
    }

}
