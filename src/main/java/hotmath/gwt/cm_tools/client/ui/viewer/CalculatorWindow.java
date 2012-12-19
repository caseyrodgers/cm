package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tutor.client.view.CalculatorPanel;


public class CalculatorWindow extends GWindow {

    private static CalculatorWindow __instance;

    public static CalculatorWindow getInstance() {
        __instance = null;
        if(__instance == null) {
            __instance = new CalculatorWindow();
        }
        return __instance;
    }

    public CalculatorWindow() {
        super(true);
        setPixelSize(350,250);
        setHeadingHtml("Calculator Window 3");
        setModal(false);
        setClosable(true);
        setResizable(false);
        
        
        setWidget(new CalculatorPanel());
    }
}
