package hotmath.gwt.search.client.ui;

import hotmath.gwt.cm_core.client.award.CmAwardPanel;
import hotmath.gwt.cm_core.client.award.CmAwardPanel.AwardCallback;
import hotmath.gwt.cm_tools.client.ui.MyIconButton;
import hotmath.gwt.cm_tools.client.ui.ShowUserProgramStatusDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

public class HeaderPanel extends FlowLayoutContainer {

    static public HeaderPanel __instance;

    Label _headerText;
    HTML _helloInfo = new HTML();
    private MyIconButton helpButton;

    private CmAwardPanel _awards;

    public HeaderPanel() {
        __instance = this;
        setStyleName("header-panel");
        _helloInfo.setStyleName("hello-info");
        _helloInfo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new ShowUserProgramStatusDialog();
            }
        });

        final SimpleContainer awardTip = new SimpleContainer();
        awardTip.setToolTip("Shows your awards for the current session");
        _awards = new CmAwardPanel(new AwardCallback() {
            public void awardPosted(int totalAwards) {
                String tip = null;
                if (totalAwards == 1) {
                    tip = "You've solved 1 problem correctly on the first attempt so far this session.";
                } else {
                    tip = "You've solved " + totalAwards
                            + " problems correctly on the first attempt so far this session.";
                }

                awardTip.setToolTip(tip);
            }
        });
        awardTip.setWidget(_awards);

        add(awardTip);
        add(_helloInfo);
        // helpButton = new MyIconButton("header-panel-help-btn");
//        helpButton.addSelectHandler(new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//
//                Event currentEvent = helpButton.getCurrentEvent();
//                if (currentEvent != null && currentEvent.getCtrlKey()) {
//                    new ShowDebugUrlWindow();
//                } else {
//                    GWT.runAsync(new CmRunAsyncCallback() {
//                        @Override
//                        public void onSuccess() {
//                            new HelpWindow();
//                        }
//                    });
//                }
//            }
//        });
//        add(helpButton);

        _headerText = new Label();
        _headerText.addStyleName("header-panel-title");
    }
}

