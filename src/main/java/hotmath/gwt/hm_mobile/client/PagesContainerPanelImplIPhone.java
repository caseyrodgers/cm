package hotmath.gwt.hm_mobile.client;


import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStackPopEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStackPushEvent;
import hotmath.gwt.cm_mobile_shared.client.util.ViewSettings;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PagesContainerPanelImplIPhone extends Composite implements PagesContainerPanel {

    private ObservableStack<IPage> mPageModel;
    private FlowPanel mBasePanel;
    private GenericContainerTag mActiveContainerPanel;
    private GenericContainerTag mInactiveContainerPanel;
    private boolean mIsIphone;

    public PagesContainerPanelImplIPhone() {
    	Log.info("PagesContainerPanelImplIPhone: startup");
    	
        mBasePanel = new FlowPanel();
        mBasePanel.getElement().setId("pages_container");

        mActiveContainerPanel = new GenericContainerTag("div");
        mInactiveContainerPanel = new GenericContainerTag("div");

        mBasePanel.add(mActiveContainerPanel);
        mBasePanel.add(mInactiveContainerPanel);

        mActiveContainerPanel.setStyleName("page");
        mInactiveContainerPanel.setStyleName("page");

        registerDomTransitionEndedEvent(mActiveContainerPanel.getElement());
        registerDomTransitionEndedEvent(mInactiveContainerPanel.getElement());

        mIsIphone = calculateIsIphone();

        Log.debug("isIPhone: " + mIsIphone);

        initWidget(mBasePanel);
    }

    @Override
    public Widget getPanel() {
    	return this;
    }
    
    private native static boolean calculateIsIphone() /*-{
        var ua = navigator.userAgent.toLowerCase();

        if (ua.indexOf("safari") != -1 && 
            ua.indexOf("applewebkit") != -1 && 
            ua.indexOf("mobile") != -1) 
        {       
            return true;
        }
        else 
        {
            return false;
        }
    }-*/;

    private native void registerDomTransitionEndedEvent(Element element) /*-{
        try
        {
            var instance = this;

            var callBack = function(e){
                instance.@hotmath.gwt.hm_mobile.client.PagesContainerPanelImplIPhone::onDomTransitionEnded()();
            };

            element.addEventListener('webkitTransitionEnd', callBack, false);   
        }
        catch (err)
        {
        }
    }-*/;

    public void onDomTransitionEnded() {
    	Log.debug("onDomTransitionEnded called");
        mInactiveContainerPanel.clear();
        mActiveContainerPanel.removeStyleName("animate");
        mInactiveContainerPanel.removeStyleName("animate");
        ViewSettings.AnimationRunning = false;
    }

    public void bind(ObservableStack<IPage> pageModel) {
        mPageModel = pageModel;

        mPageModel
                .addHandler(new ObservableStackPopEvent.ObservableStackPopHandler<IPage>() {
                    @Override
                    public void itemPoped(ObservableStackPopEvent<IPage> e) {
                        IPage page = e.getItemPoped();
                        TokenParser tp = page.getBackButtonLocation();
                        if(tp != null) {
                            mPageModel.removeAll();
                            History.newItem(tp.getHistoryTag());
                        }
                        else {
                            removePage(page);
                        }
                    }
                });

        mPageModel
                .addHandler(new ObservableStackPushEvent.ObservableStackPushHandler<IPage>() {
                    @Override
                    public void itemPushed(ObservableStackPushEvent<IPage> e) {
                        addPage(e.getItemPushed());
                    }
                });

        for (IPage p : pageModel) {
            addPage(p);
        }
    }

    public void addPage(IPage p) {

        final GenericContainerTag oldContainer = mActiveContainerPanel;
        final GenericContainerTag newContainer = mInactiveContainerPanel;

        AbstractPagePanel pagePanel = PagePanelFactory.createPagePanel(p);

        newContainer.clear();

        boolean startedAnimation = false;

        if (oldContainer.getWidgetCount() > 0) {

            newContainer.removeStyleName("animate");
            newContainer.removeStyleName("left");
            newContainer.addStyleName("right");

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            	@Override
            	public void execute() {
                    oldContainer.addStyleName("animate");
                    oldContainer.addStyleName("left");
                    oldContainer.removeStyleName("right");

                    newContainer.addStyleName("animate");
                    newContainer.removeStyleName("left");
                    newContainer.removeStyleName("right");
            	}
            });
            startedAnimation = true;

        } else {
            oldContainer.removeStyleName("animate");
            oldContainer.addStyleName("left");
            oldContainer.removeStyleName("right");

            newContainer.removeStyleName("animate");
            newContainer.removeStyleName("left");
            newContainer.removeStyleName("right");

        }

        newContainer.add(pagePanel);

        mActiveContainerPanel = newContainer;
        mInactiveContainerPanel = oldContainer;

        if (mIsIphone && startedAnimation) {
            ViewSettings.AnimationRunning = true;
        }
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_PAGE_LOADED, p));
    }

    public void removePage(IPage removedPage) {
        
        //Window.alert("Removing: " + removedPage);
        final GenericContainerTag oldContainer = mActiveContainerPanel;
        final GenericContainerTag newContainer = mInactiveContainerPanel;

        AbstractPagePanel pagePanel = PagePanelFactory.createPagePanel(mPageModel.peek());

        newContainer.clear();

        newContainer.removeStyleName("animate");
        newContainer.addStyleName("left");
        newContainer.removeStyleName("right");

        DeferredCommand.addCommand(new Command() {
            @Override
            public void execute() {
                oldContainer.addStyleName("animate");
                oldContainer.removeStyleName("left");
                oldContainer.addStyleName("right");

                newContainer.addStyleName("animate");
                newContainer.removeStyleName("left");
                newContainer.removeStyleName("right");

            }
        });

        newContainer.add(pagePanel);

        mActiveContainerPanel = newContainer;
        mInactiveContainerPanel = oldContainer;

        if (mIsIphone) {
            ViewSettings.AnimationRunning = true;
        }
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_PAGE_REMOVED, removedPage));
    }
}
