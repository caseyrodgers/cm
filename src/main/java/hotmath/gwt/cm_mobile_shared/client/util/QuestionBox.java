package hotmath.gwt.cm_mobile_shared.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

/** Provide a standard dialog to ask a question
 *  and be asynchronously called back when complete.
 *  
 * @author casey
 *
 */
public class QuestionBox extends DialogBox {
    
    
    static public void askYesNoQuestion(String title, String question, CallBack callBack) {
        QuestionBox qb = getInstance();
        qb.showQuestion(title, question, callBack);        
    }
    
    
    static public QuestionBox getInstance() {
        if(__instance==null) {
            __instance = new QuestionBox();
        }
        return __instance;
    }
    static private  QuestionBox __instance;
    
    CallBack callBack;
    private static QuestionBoxUiBinder uiBinder = GWT.create(QuestionBoxUiBinder.class);

    interface QuestionBoxUiBinder extends UiBinder<Widget, QuestionBox> {
    }

    private QuestionBox() {
        addStyleName("questionBox");
        setWidget(uiBinder.createAndBindUi(this));
        setAnimationEnabled(true);
        setAutoHideEnabled(false);
        setGlassEnabled(true);
        setSize("300px", "200px");
        setModal(true);
    }

    public void showQuestion(String title, String question, CallBack callBack) {
        this.callBack = callBack;
        setText(title);
        questionContent.setInnerHTML(question);
        setModal(true);
        center();
        setVisible(true);
    }

    static public interface CallBack {
        void onSelectYes();
    }
    
    
    @UiField
    Button yesButton, noButton;
    
    @UiField
    ParagraphElement questionContent;
    
    @UiHandler("yesButton")
    void yesButtonHandler(ClickEvent ce) {
        callBack.onSelectYes();
        setModal(false);
        setVisible(false);
        
    }
    
    @UiHandler("noButton")
    void noButtonHandler(ClickEvent ce) {
        setModal(false);
        setVisible(false);
    }
}
