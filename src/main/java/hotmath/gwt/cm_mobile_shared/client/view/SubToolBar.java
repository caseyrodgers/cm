package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;

import com.google.gwt.user.client.ui.FlowPanel;

public class SubToolBar extends FlowPanel {
    private TouchButton _yourProgram;

    public SubToolBar(boolean showReturnToProgram) {
        super();
        addStyleName("SubToolBar");
        
//        if(showReturnToProgram) {
//            _yourProgram = new SexyButton("Exit Assignments", new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent event) {
//                    CmRpcCore.EVENT_BUS.fireEvent(new HandleNextFlowEvent(SharedData.getMobileUser().getFlowAction()));                    
//                }
//            });
//            _yourProgram.addStyleName("instruction-button");
//            add(_yourProgram);
//        }
    }

    public void showReturnTo(boolean b) {
        if(_yourProgram != null) {
            _yourProgram.setVisible(b);
        }
    }
}