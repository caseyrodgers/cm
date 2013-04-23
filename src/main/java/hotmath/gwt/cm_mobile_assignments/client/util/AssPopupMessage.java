package hotmath.gwt.cm_mobile_assignments.client.util;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.mgwt.ui.client.dialog.Dialogs;
import com.googlecode.mgwt.ui.client.dialog.Dialogs.ButtonType;
import com.googlecode.mgwt.ui.client.dialog.Dialogs.OptionCallback;
import com.googlecode.mgwt.ui.client.dialog.Dialogs.OptionsDialogEntry;

public class AssPopupMessage {
    
    public AssPopupMessage() {
        
        List<OptionsDialogEntry> list = new ArrayList<OptionsDialogEntry>();
        list.add(new OptionsDialogEntry("Confirm", ButtonType.CONFIRM));
        list.add(new OptionsDialogEntry("Cancel", ButtonType.NORMAL));
        list.add(new OptionsDialogEntry("Delete", ButtonType.IMPORTANT));

        Dialogs.options(list, new OptionCallback() {
            @Override
            public void onOptionSelected(int index) {
                AssAlertBox.showAlert("Poupp: " + index);
            }
        });
    }
    
}
