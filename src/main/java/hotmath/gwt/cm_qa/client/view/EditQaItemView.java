package hotmath.gwt.cm_qa.client.view;

import hotmath.gwt.cm_qa.client.CmQa;
import hotmath.gwt.cm_qa.client.model.QaEntryModelGxt;
import hotmath.gwt.cm_qa.client.rpc.UpdateQaItemAction;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EditQaItemView extends CmWindow {
	
	QaEntryModelGxt model;
	TextArea textArea = new TextArea();

	public EditQaItemView(final QaEntryModelGxt model, final CallbackOnComplete callback) {
		super();
		
		this.model = model;
		setLayout(new FitLayout());
		textArea.setValue(model.getDescription());
		add(textArea);
		setHeading("Edit QA Item");
		setSize(400,300);
		
		addButton(new Button("Save", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				final String description = textArea.getValue();
				UpdateQaItemAction action = new UpdateQaItemAction(model.getItem(), description);
				CmQa.getCmService().execute(action, new AsyncCallback<RpcData>() {
					@Override
					public void onSuccess(RpcData arg0) {
						model.setDescription(description);
						close();
						
						callback.isComplete();
					}
					@Override
					public void onFailure(Throwable arg0) {
						arg0.printStackTrace();
						Window.alert("Error saving: " + arg0.getMessage());
					}
				});
			}
		}));		
		addCloseButton();
		setModal(true);
		setVisible(true);
	}

}
