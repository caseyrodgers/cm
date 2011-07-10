package hotmath.gwt.hm_mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class FeedbackDialog extends DialogBox  {
	
    private static FeedbackDialogUiBinder uiBinder = GWT.create(FeedbackDialogUiBinder.class);

    interface FeedbackDialogUiBinder extends UiBinder<Widget, FeedbackDialog> {
    }

	
	public FeedbackDialog() {
		super(true);
		setSize("300px", "200px");
		setText("Feedback");		
		addStyleName("feedback-dialog");
		setGlassEnabled(true);
		setWidget(uiBinder.createAndBindUi(this));
		setVisible(true);
	}
	
	public void showCentered() {
		center();
	}
}
