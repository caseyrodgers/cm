package hotmath.gwt.cm_mobile3.client.ui;



import hotmath.gwt.cm_core.client.event.CmQuizModeActivatedEvent;
import hotmath.gwt.cm_core.client.event.CmQuizModeActivatedEventHandler;
import hotmath.gwt.cm_core.client.model.SearchAllowMode;
import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonActivity;
import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceTutorActivity;
import hotmath.gwt.cm_mobile3.client.resource.MyResources;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.HeaderTitleChangedEvent;
import hotmath.gwt.cm_mobile_shared.client.event.HeaderTitleChangedHandler;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.NewPageLoadedEvent;
import hotmath.gwt.cm_mobile_shared.client.event.NewPageLoadedHandler;
import hotmath.gwt.cm_mobile_shared.client.event.UserLoginEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLoginHandler;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutEvent;
import hotmath.gwt.cm_mobile_shared.client.event.UserLogoutHandler;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.IPage.ApplicationType;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchAnchor;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStackPopEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStackPushEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.search.SearchButton;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
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

    Anchor _logout;

    MyResources resources = GWT.create(MyResources.class);
    
    FlowPanel basePanel;
    
    
    AssignmentButtonIndicator _assignmentButton = new AssignmentButtonIndicator();
    MobileSearchButton _searchButton = new MobileSearchButton();
    private TouchAnchor _calcButton;
    public HeaderPanel(EventBus eventBus) {
        
        basePanel = new FlowPanel();
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

        _logout = new TouchAnchor("Logout");
        _logout.addStyleName("logout-button");
        _logout.addStyleName("about-button");
        _logout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CmRpcCore.EVENT_BUS.fireEvent(new UserLogoutEvent());
            }
        });
        _logout.setVisible(false);
        basePanel.add(_logout);
        
        
        _calcButton = new TouchAnchor("Calc");
        _calcButton.addStyleName("calc-button");
        _logout.addStyleName("about-button");
        _calcButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CalculatorWindowForMobile.showSharedInstance();
            }
        });
        _calcButton.setVisible(true);
        basePanel.add(_calcButton);

        
        TouchAnchor about = new TouchAnchor();
        about.addStyleName("about-button");
        about.getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/icon-info.png'/>");
        about.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new AboutDialog().showCentered();                
            }
        });
        basePanel.add(about);

        _assignmentButton.setVisible(false);        
        basePanel.add(_assignmentButton);
        
        
        basePanel.add(_searchButton);

        registerDomTransitionEndedEvent(mActiveTitle.getElement());
        registerDomTransitionEndedEvent(mInactiveTitle.getElement());

//        TouchButton stressTest = new TouchButton("Test", new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                runStressTest();
//            }
//        });

        //basePanel.add(stressTest);
        initWidget(basePanel);

        /** Show showing the Welcome panel turn on the Logout button */
        eventBus.addHandler(UserLoginEvent.TYPE, new UserLoginHandler() {
            @Override
            public void userLogin(final CmMobileUser user) {
                
                if(user.getUserInfo().getSearchAllowMode() == SearchAllowMode.DISABLED_ALWAYS) {
                    _searchButton.setAllowSearch(false);
                }
                else {
                    CmRpcCore.EVENT_BUS.addHandler(CmQuizModeActivatedEvent.TYPE,  new CmQuizModeActivatedEventHandler() {
                        @Override
                        public void quizModeActivated(boolean yesNo) {
                            if(user.getUserInfo().getSearchAllowMode() != SearchAllowMode.ENABLED_ALWAYS) {
                                _searchButton.setAllowSearch(!yesNo);
                            }
                        }
                    });
                }        
                showLogoutButton(true);
            }
        });

        eventBus.addHandler(UserLogoutEvent.TYPE, new UserLogoutHandler() {
            @Override
            public void userLogout() {
                showLogoutButton(false);
            }
        });
        
        
        eventBus.addHandler(HeaderTitleChangedEvent.TYPE, new HeaderTitleChangedHandler() {
            @Override
            public void headerTitleChanged(String title) {
                mActiveTitle.setText(title);
            }
        });
        
        eventBus.addHandler(NewPageLoadedEvent.TYPE,  new NewPageLoadedHandler() {
            @Override
            public void pageLoaded(IPage page) {
                setupDomainSpecificButtons(page);
            }
            
            @Override
            public void pageLoaded() {
            }
        });
    }

    
    protected void setupDomainSpecificButtons(IPage page) {
        AssignmentUserInfo ad = SharedData.getMobileUser().getAssignmentInfo();
        if(!ad.isAdminUsingAssignments()) {
            _assignmentButton.setVisible(false);
        }
        else {
            if(page.getApplicationType() == ApplicationType.PROGRAM) {
                _assignmentButton.setVisible(true);
            }
            else if(page.getApplicationType() == ApplicationType.ASSIGNMENT) {
                _assignmentButton.setVisible(true);
            }
            else {
                _assignmentButton.setVisible(false);
            }
        }
    }

    public void showLogoutButton(boolean yesNo) {
        _logout.setVisible(yesNo);
    }

    private native void registerDomTransitionEndedEvent(Element element) /*-{
                                                                         try
                                                                         {
                                                                         var instance = this;

                                                                         var callBack = function(e){
                                                                         instance.@hotmath.gwt.cm_mobile3.client.ui.HeaderPanel::onDomTransitionEnded()();
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
                changeHeaderBackwards(topPage.getViewTitle(), topPage.getBackButtonText());
            }
        });

        mPageStack.addHandler(new ObservableStackPushEvent.ObservableStackPushHandler<IPage>() {
            @Override
            public void itemPushed(ObservableStackPushEvent<IPage> e) {
                changeHeaderForwards(e.getItemPushed().getViewTitle(), e.getItemPushed().getBackButtonText());
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

            mActiveButton.getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/back_button.png'/><span>" + backButtonText + "</span>");

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

            newButton.getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/back_button.png'/><span>" + backButtonText + "</span>");

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
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

        return isOverflow(width, withButton, BUTTON_SPACE, SCREEN_WIDTH) ? CssLandscapeOverflow.On : CssLandscapeOverflow.Off;
    }

    private static CssPortraitOverflow getPortraitOverflow(int width, boolean withButton) {
        final int BUTTON_SPACE = 87;
        final int SCREEN_WIDTH = 320;

        return isOverflow(width, withButton, BUTTON_SPACE, SCREEN_WIDTH) ? CssPortraitOverflow.On : CssPortraitOverflow.Off;
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
        newButton.getElement().setInnerHTML("<img src='/gwt-resources/images/mobile/back_button.png'/><span>" + backButtonText + "</span>");
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

    int  _stressCount=0;
    final static int  WAIT = 2000;
    private void runStressTest() {
        
        if(_stressCount++ < 100) {

            InmhItemData itemData = new InmhItemData(CmResourceType.PRACTICE, "cmextras_1_6_1_16_6", "Test pid");
            final PrescriptionLessonResourceTutorActivity activity = new PrescriptionLessonResourceTutorActivity(CatchupMathMobile3.__clientFactory.getEventBus(), itemData);

            PrescriptionLessonResourceTutorView view = CatchupMathMobile3.__clientFactory.getPrescriptionLessonResourceTutorView();
            view.setHeaderTitle("Required " + itemData.getTitle());
            view.setPresenter(activity);
            CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage) view));

            new com.google.gwt.user.client.Timer() {
                @Override
                public void run() {
                    showWhiteboard();
                    new Timer() {
                        public void run() {
                            PrescriptionLessonActivity activity = new PrescriptionLessonActivity(CatchupMathMobile3.__clientFactory, CatchupMathMobile3.__clientFactory.getEventBus());
                            PrescriptionLessonView view = CatchupMathMobile3.__clientFactory.getPrescriptionLessonView();
                            view.setPresenter(activity);
                            CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage) view));
                            
                            new Timer() {
                                @Override
                                public void run() {
                                    runStressTest();
                                }
                            }.schedule(WAIT);
                        }
                }.schedule(WAIT);
            }}.schedule(WAIT);
        }
    }
    
    private native void showWhiteboard() /*-{
        $wnd.TutorManager.showWhiteboard();
    }-*/;
    
    
}
