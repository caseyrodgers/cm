package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;



class ShowHowToUseDialog extends GWindow {
    public ShowHowToUseDialog() {
        super(true);
        setModal(true);
        setPixelSize(350,200);
        setHeadingText("How To Use This");
        add(new HTML(html));
    }
    String html =
        "<div style='padding: 10px 5px;'>" +
         "<p>Work out your answer using pencil and paper or the Whiteboard. " +
         "Some teachers may require this.</p>" +
         "<p>If you see an 'Enter Your Answer' input box, you can enter your " +
         "answer there to check your work.</p>" +
        "</div>";
}
