package hotmath.gwt.shared.client.util;

public class NotActiveProgramWindow extends StandardSystemRefreshWindow {
    public NotActiveProgramWindow() {
        super("Program Changed", "Your program has been changed. " +
                                 "Please click the button below to start your new program.");
        setVisible(true);
    }
}
