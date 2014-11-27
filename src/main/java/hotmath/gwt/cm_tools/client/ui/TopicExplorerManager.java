package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.Topic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig.ToolTipRenderer;

public class TopicExplorerManager extends GWindow {
    
    static private TopicExplorerManager __instance;
    static public TopicExplorerManager getInstance() {
        if(__instance == null) {
            __instance = new TopicExplorerManager();
        }
        return __instance;
    }

    static public boolean isInitialized() {
        return __instance != null;
    }
    
    TabPanel _tabPanel = new TabPanel();
    SearchPanel _searchPanel = new SearchPanel();
    private TopicExplorerManager() {
        super(true);
        
        setPixelSize(850, 650);
        setHeadingText("Catchup Math Topic Explorer");
        setMaximizable(true);
        //setMinimizable(true);;
        setCollapsible(true);
        
        setModal(false);
        
        _tabPanel.add(_searchPanel, new TabItemConfig("Search",  false));
        setWidget(_tabPanel);
        
//        addTool(new TextButton("Search", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//                new SearchPanel().showWindow();
//            }
//        }));
        
        
        
        
        setVisible(true);
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                _searchPanel._inputBox.focus();
            }
        });
        
        
        addTool(createTtButton());
        
    }
    
    ToolTipConfig config;
    public interface TtRenderer extends ToolTipConfig.ToolTipRenderer<Object>, XTemplates {    	 
        @Override
        @XTemplate(source = "TopicExplorerManagerToolTip.html")
        public SafeHtml renderToolTip(Object data);
    }
    
    private TtRenderer renderer = GWT.create(TtRenderer.class);
    
    private Widget createTtButton() {
		
    	TextButton btn = new TextButton("Custom");
        config = new ToolTipConfig();
        config.setBodyHtml("Prints the current document");
        config.setTitleHtml("Template Tip");
        config.setMouseOffsetX(0);
        config.setMouseOffsetY(0);
        config.setAnchor(Side.LEFT);
        config.setRenderer(renderer);
        config.setCloseable(true);
        config.setMaxWidth(415);
        btn.setToolTipConfig(config);
        
        return btn;
	}

	public void exploreTopic(Topic topic) {
        Widget panel = new TopicExplorer(topic).asWidget();
        _tabPanel.add(panel, new TabItemConfig(topic.getName(),  true));
        _tabPanel.setActiveWidget(panel);
        setVisible(true);
    }


    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                Topic topic = new Topic("Graphing Square Root Functions", "topics/graphing-square-root-functions.html",null);
                TopicExplorerManager.getInstance();
                //new TopicExplorerManager().exploreTopic(topic);
            }
        });
    }

}
