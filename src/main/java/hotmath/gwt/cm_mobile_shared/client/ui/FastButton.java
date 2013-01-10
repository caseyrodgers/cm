package hotmath.gwt.cm_mobile_shared.client.ui;

import hotmath.gwt.fastpress.client.TestFastPressElement;
import hotmath.gwt.fastpress.client.fast.PressHandler;

public class FastButton extends TestFastPressElement {
    public FastButton() {
        super("No label");

    }
    public FastButton(String text, PressHandler pressHandler) {
        super(text, pressHandler);
    }

}

