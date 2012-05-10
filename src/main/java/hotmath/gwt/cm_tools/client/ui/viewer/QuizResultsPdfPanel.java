package hotmath.gwt.cm_tools.client.ui.viewer;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.SimplePanel;

public class QuizResultsPdfPanel extends SimplePanel {

    public QuizResultsPdfPanel(String pdfFileUrl) {
        showResultsAsPdf(pdfFileUrl);
    }

    private void showResultsAsPdf(String pdfUrl) {
        Frame frame = new Frame();
        frame.setWidth("98%");
        frame.setHeight("400px");
        frame.setUrl(pdfUrl);
        add(frame);
    }
}
