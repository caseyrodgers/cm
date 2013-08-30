package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class HaveYouCheckedYourWorkWindow extends GWindow {

    public HaveYouCheckedYourWorkWindow(final Callback callback) {
        super(false);
        
        setPixelSize(330, 120);
        addStyleName("quiz-context-msg-window");
        setClosable(false);

        setModal(true);
        setHeadingHtml("Ready to Check Quiz?");
        add(new HTML("<p style='padding: 15px 10px;'>Did you work out your answer to all the questions carefully?</p>"));

        addButton(new TextButton("Yes", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();                
                callback.quizIsReadyToBeChecked();
            }
        }));

        addButton(new TextButton("No", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
                callback.quizWasCanceled();
            }
        }));

        setVisible(true);
    }
    
    static public interface Callback {
        void quizIsReadyToBeChecked();
        void quizWasCanceled();
    }
}
