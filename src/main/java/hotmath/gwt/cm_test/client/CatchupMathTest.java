package hotmath.gwt.cm_test.client;

import hotmath.gwt.cm_mobile_shared.client.QuizPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMathTest implements EntryPoint {
    public void onModuleLoad() {
        QuizPanel qp = new QuizPanel(null);
        Window.alert("Quiz Panel: " + qp);
    }
}