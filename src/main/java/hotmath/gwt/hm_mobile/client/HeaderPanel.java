package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStackPopEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStackPushEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;
import hotmath.gwt.hm_mobile.client.event.ResetListSelectionsEvent;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HeaderPanel extends Composite {

    private GenericTextTag<String> mActiveButton;
    private GenericTextTag<String> mInactiveButton;
    private Label mActiveTitle;
    private Label mInactiveTitle;
    HandlerRegistration mActiveButtonHandlerRegistration;

    private boolean mHasLeftButton;

    private ObservableStack<IPage> mPageStack;
    // private Screen mScreen;

    private boolean mInitialized = false;

    public HeaderPanel() {
        
        FlowPanel basePanel = new FlowPanel();
        basePanel.getElement().setId("header");

        mActiveButton = new GenericTextTag<String>("div");
        mActiveButton.setStyleName("backButton");
        configureActiveButtonEvent();
        basePanel.add(mActiveButton);

        mInactiveButton = new GenericTextTag<String>("div");
        mInactiveButton.setStyleName("backButton");
        basePanel.add(mInactiveButton);

        mActiveTitle = new Label();
        mActiveTitle.setStyleName("title");
        basePanel.add(mActiveTitle);

        mInactiveTitle = new Label();
        mInactiveTitle.setStyleName("title");
        basePanel.add(mInactiveTitle);
        

        Anchor about = new Anchor();
        about.getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info.png'/>");
        about.addStyleName("about-dialog");
        about.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new AboutDialog().showCentered();
			}
		});
        basePanel.add(about);
        
        registerDomTransitionEndedEvent(mActiveTitle.getElement());
        registerDomTransitionEndedEvent(mInactiveTitle.getElement());

        initWidget(basePanel);
    }

    private native void registerDomTransitionEndedEvent(Element element) /*-{
                                                                         try
                                                                         {
                                                                         var instance = this;

                                                                         var callBack = function(e){
                                                                         instance.@hotmath.gwt.hm_mobile.client.HeaderPanel::onDomTransitionEnded()();
                                                                         };

                                                                         element.addEventListener('webkitTransitionEnd', callBack, false);  
                                                                         }
                                                                         catch (err)
                                                                         {
                                                                         }
                                                                         }-*/;

    public void onDomTransitionEnded() {
        setCssClass(mActiveTitle, CssAnimate.Off);
        setCssClass(mInactiveTitle, CssAnimate.Off);
        setCssClass(mActiveButton, CssAnimate.Off);
        setCssClass(mInactiveButton, CssAnimate.Off);
    }

    private void configureActiveButtonEvent() {

        if (mActiveButtonHandlerRegistration != null) {
            mActiveButtonHandlerRegistration.removeHandler();
        }

        mActiveButtonHandlerRegistration = mActiveButton.addHandler(new TouchClickEvent.TouchClickHandler<String>() {
            @Override
            public void touchClick(TouchClickEvent<String> tag) {
            	Log.debug("TouchClick event fired: " + ViewSettings.AnimationRunning);
            	mActiveButton.addStyleName("backClicked");
            	
                if (!ViewSettings.AnimationRunning) {
                	Controller.navigateBack();
                }
            }
        });
    }

    public void bind(ObservableStack<IPage> pageStack) {
        mPageStack = pageStack;

        mPageStack.addHandler(new ObservableStackPopEvent.ObservableStackPopHandler<IPage>() {
            @Override
            public void itemPoped(ObservableStackPopEvent<IPage> e) {
                IPage topPage = mPageStack.peek();
                changeHeaderBackwards(topPage.getTitle(), topPage.getBackButtonText());
            }
        });

        mPageStack.addHandler(new ObservableStackPushEvent.ObservableStackPushHandler<IPage>() {
            @Override
            public void itemPushed(ObservableStackPushEvent<IPage> e) {
                changeHeaderForwards(e.getItemPushed().getTitle(), e.getItemPushed().getBackButtonText());
            }
        });

    }

    private enum CssPos {
        Left, Center, Right
    }

    private enum CssAnimate {
        On, Off
    }

    private enum CssButton {
        On, Off
    }

    private enum CssPortraitOverflow {
        On, Off
    }

    private enum CssLandscapeOverflow {
        On, Off
    }

    private static void setCssClass(Widget w, CssPos pos) {
        w.removeStyleName("pos-left");
        w.removeStyleName("pos-center");
        w.removeStyleName("pos-right");

        if (pos == CssPos.Left)
            w.addStyleName("pos-left");
        else if (pos == CssPos.Center)
            w.addStyleName("pos-center");
        else if (pos == CssPos.Right)
            w.addStyleName("pos-right");
    }

    private static void setCssClass(Widget w, CssAnimate animate) {
        w.removeStyleName("animate-on");
        w.removeStyleName("animate-off");

        if (animate == CssAnimate.On)
            w.addStyleName("animate-on");
        else if (animate == CssAnimate.Off)
            w.addStyleName("animate-off");
    }

    private static void setCssClass(Label w, CssButton button) {
        w.removeStyleName("button-on");
        w.removeStyleName("button-off");

        if (button == CssButton.On)
            w.addStyleName("button-on");
        else if (button == CssButton.Off)
            w.addStyleName("button-off");
    }

    private static void setCssClass(Label w, CssPortraitOverflow overflow) {
        w.removeStyleName("portrait-overflow-on");
        w.removeStyleName("portrait-overflow-off");

        if (overflow == CssPortraitOverflow.On)
            w.addStyleName("portrait-overflow-on");
        else if (overflow == CssPortraitOverflow.Off)
            w.addStyleName("portrait-overflow-off");
    }

    private static void setCssClass(Label w, CssLandscapeOverflow overflow) {
        w.removeStyleName("landscape-overflow-on");
        w.removeStyleName("landscape-overflow-off");

        if (overflow == CssLandscapeOverflow.On)
            w.addStyleName("landscape-overflow-on");
        else if (overflow == CssLandscapeOverflow.Off)
            w.addStyleName("landscape-overflow-off");
    }

    private static CssButton toCssButton(boolean hasButton) {
        return hasButton ? CssButton.On : CssButton.Off;
    }

    private void changeHeaderForwards(String titleText, String backButtonText) {
        if (titleText == null || titleText.trim() == "") {
            throw new NullPointerException("The title must not be empty.");
        }

        backButtonText = backButtonText == null ? "" : backButtonText;

        mHasLeftButton = !backButtonText.equals("");

        if (!mInitialized) {
            // the header hasn't been initialized yet.

            setCssClass(mInactiveButton, CssPos.Left);
            setCssClass(mInactiveButton, CssAnimate.Off);

            setCssClass(mInactiveTitle, CssPos.Left);
            setCssClass(mInactiveTitle, CssAnimate.Off);
            setCssClass(mInactiveTitle, toCssButton(false));

            setCssClass(mActiveButton, mHasLeftButton ? CssPos.Center : CssPos.Left);
            setCssClass(mActiveButton, CssAnimate.Off);
            mActiveButton.setText(backButtonText);

            setCssClass(mActiveTitle, CssPos.Center);
            setCssClass(mActiveTitle, CssAnimate.Off);
            setCssClass(mActiveTitle, toCssButton(mHasLeftButton));
            mActiveTitle.setText(titleText);

            // calculate width with no CSS classes:
            mActiveButton.setWidth("auto");
            int width = mActiveTitle.getOffsetWidth();
            mActiveButton.setWidth("");

            setCssClass(mActiveTitle, getLandscapeOverflow(width, mHasLeftButton));
            setCssClass(mActiveTitle, getPortraitOverflow(width, mHasLeftButton));

            mInitialized = true;
        } else {
            final GenericTextTag<String> oldButton = mActiveButton;
            final GenericTextTag<String> newButton = mInactiveButton;

            final Label oldTitle = mActiveTitle;
            final Label newTitle = mInactiveTitle;

            final boolean hasLeftButton = mHasLeftButton;

            // int buttonWidth = oldButton.getElement().getOffsetWidth();

            setCssClass(newButton, CssPos.Right);
            setCssClass(newButton, CssAnimate.Off);
            newButton.setText(backButtonText);

            setCssClass(newTitle, CssPos.Right);
            setCssClass(newTitle, CssAnimate.Off);
            setCssClass(newTitle, toCssButton(mHasLeftButton));
            newTitle.setText(titleText);

            // calculate width with no CSS classes:
            newTitle.setWidth("auto");
            int width = newTitle.getOffsetWidth();
            newTitle.setWidth("");

            setCssClass(newTitle, getLandscapeOverflow(width, mHasLeftButton));
            setCssClass(newTitle, getPortraitOverflow(width, mHasLeftButton));

            DeferredCommand.addCommand(new Command() {
                @Override
                public void execute() {
                    setCssClass(oldButton, CssPos.Left);
                    setCssClass(oldButton, CssAnimate.On);

                    if (hasLeftButton) {
                        setCssClass(newButton, CssPos.Center);
                        setCssClass(newButton, CssAnimate.On);
                    }

                    setCssClass(oldTitle, CssPos.Left);
                    setCssClass(oldTitle, CssAnimate.On);

                    setCssClass(newTitle, CssPos.Center);
                    setCssClass(newTitle, CssAnimate.On);

                    /*
                     * DeferredCommand.addCommand(new Command() {
                     * 
                     * @Override public void execute() { onDomTransitionEnded();
                     * } });
                     */
                }
            });

            mActiveButton = newButton;
            mInactiveButton = oldButton;
            mActiveTitle = newTitle;
            mInactiveTitle = oldTitle;

        }

        configureActiveButtonEvent();

    }

    private static CssLandscapeOverflow getLandscapeOverflow(int width, boolean withButton) {
        final int BUTTON_SPACE = 117;
        final int SCREEN_WIDTH = 480;

        return isOverflow(width, withButton, BUTTON_SPACE, SCREEN_WIDTH) ? CssLandscapeOverflow.On
                : CssLandscapeOverflow.Off;
    }

    private static CssPortraitOverflow getPortraitOverflow(int width, boolean withButton) {
        final int BUTTON_SPACE = 87;
        final int SCREEN_WIDTH = 320;

        return isOverflow(width, withButton, BUTTON_SPACE, SCREEN_WIDTH) ? CssPortraitOverflow.On
                : CssPortraitOverflow.Off;
    }

    private static boolean isOverflow(int width, boolean withButton, int buttonSpace, int screenWidth) {

        if (withButton) {
            if ((screenWidth - width) / 2 < buttonSpace) {
                return true;
            }
            return false;
        }

        return screenWidth - width < 0;
    }

    private void changeHeaderBackwards(String titleText, String backButtonText) {
        if (titleText == null || titleText.trim() == "") {
            throw new NullPointerException("The title must not be empty.");
        }

        backButtonText = backButtonText == null ? "" : backButtonText;

        mHasLeftButton = !backButtonText.equals("");

        final boolean hasLeftButton = mHasLeftButton;

        final GenericTextTag<String> oldButton = mActiveButton;
        final GenericTextTag<String> newButton = mInactiveButton;

        final Label oldTitle = mActiveTitle;
        final Label newTitle = mInactiveTitle;

        setCssClass(newButton, CssPos.Left);
        setCssClass(newButton, CssAnimate.Off);
        newButton.setText(backButtonText);

        setCssClass(newTitle, CssPos.Left);
        setCssClass(newTitle, CssAnimate.Off);
        setCssClass(newTitle, toCssButton(mHasLeftButton));
        newTitle.setText(titleText);

        // calculate the width with all classes removed.
        newTitle.setWidth("auto");
        int width = newTitle.getOffsetWidth();
        newTitle.setWidth("");

        setCssClass(newTitle, getLandscapeOverflow(width, mHasLeftButton));
        setCssClass(newTitle, getPortraitOverflow(width, mHasLeftButton));

        DeferredCommand.addCommand(new Command() {
            @Override
            public void execute() {
                setCssClass(oldButton, CssPos.Right);
                setCssClass(oldButton, CssAnimate.On);

                setCssClass(oldTitle, CssPos.Right);
                setCssClass(oldTitle, CssAnimate.On);

                setCssClass(newTitle, CssPos.Center);
                setCssClass(newTitle, CssAnimate.On);

                if (hasLeftButton) {
                    setCssClass(newButton, CssPos.Center);
                    setCssClass(newButton, CssAnimate.On);
                }

                /*
                 * DeferredCommand.addCommand(new Command() {
                 * 
                 * @Override public void execute() { onDomTransitionEnded(); }
                 * });
                 */
            }
        });

        mActiveButton = newButton;
        mInactiveButton = oldButton;
        mActiveTitle = newTitle;
        mInactiveTitle = oldTitle;

        configureActiveButtonEvent();

    }
}

