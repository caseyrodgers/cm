package hotmath.gwt.cm_mobile3.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/** Show dialog after user has correctly answered widget.
 * 
 * Provide choice of options:
 * 
 *   1. Continue with tutor
 *   2. expand entire tutor
 *   3. return to lesson
 *   
 *   in any case the RPP is marked as complete
 *   
 * @author casey
 *
 */
public class TutorWidgetCorrectDialog extends Composite {
    
    static public interface Callback {
        void returnToLesson();
    }

    private Callback callBack;

    interface MyUiBinder extends UiBinder<Widget, TutorWidgetCorrectDialog> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);


    private TutorWidgetCorrectDialog(Callback callBack) {
        this.callBack = callBack;
        
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    
    public static void showDialog(Callback callBack) {
        DialogBox dialog = new DialogBox(false, true);
        dialog.setAnimationEnabled(true);
        dialog.setGlassEnabled(true);
        
        dialog.setWidget(new TutorWidgetCorrectDialog(callBack));

        
        dialog.setSize("300px",  "300px");

        dialog.center();
        dialog.show();
    }

}
