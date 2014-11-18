package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.data.client.loader.JsoReader;
import com.sencha.gxt.data.client.loader.ScriptTagProxy;
import com.sencha.gxt.data.client.writer.UrlEncodingWriter;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent.BeforeLoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.info.Info;

public class SearchPanel implements IsWidget {
    
    interface TopicTest {
        String getTopic();
    }
    
    interface JsonResources extends ClientBundle {
        
        static final JsonResources INSTANCE = GWT.create(JsonResources.class);

        @Source("SearchWindow.json")
        TextResource place();
    } 
    
    public interface TopicTestAutoBeanFactory extends AutoBeanFactory {
        AutoBean<TopicTest> topic();
    }
    GWindow window;

    public SearchPanel() {
//        TopicTestAutoBeanFactory factory = GWT.create(TopicTestAutoBeanFactory.class);
//        AutoBean<TopicTest> bean = AutoBeanCodex.decode(factory, TopicTest.class, JsonResources.INSTANCE.place().getText());
//        TopicTest widget = bean.as();
//        String t = widget.getTopic();
//        Splittable s = AutoBeanCodex.encode(bean);
//        String jsonData = s.getPayload();
    }
    
    public void showWindow() {
        this.window = new GWindow(true);
        this.window.setHeadingText("Catchup Math Topic Search");
        this.window.setPixelSize(550, 150);
        this.window.setResizable(false);
        this.window.setWidget(this);
        this.window.setVisible(true);
    }

    interface Bundle extends ClientBundle {
        @Source("SearchWindow.css")
        ExampleStyle css();
    }

    interface ExampleStyle extends CssResource {
        String searchItem();
    }

    interface ExampleTemplate extends XTemplates {
        // @XTemplate("<div class='{style.searchItem}'><h3><span>{post.date:date(\"M/d/yyyy\")}<br />by {post.author}</span>{post.title}</h3>{post.excerpt}</div>")
        @XTemplate("<div class='{style.searchItem}'><h3>{post.name}</span></h3>{post.excerpt}</div>")
        SafeHtml render(Topic post, ExampleStyle style);
    }

    interface TopicAutoBeanFactory extends AutoBeanFactory {
        static TopicAutoBeanFactory instance = GWT.create(TopicAutoBeanFactory.class);
        AutoBean<ForumCollection> dataCollection();
        AutoBean<ForumListLoadResult> dataLoadResult();
        AutoBean<TopicLoadConfig> loadConfig();
    }

    public interface Topic {
        @PropertyName("file")
        public String getFile();

        @PropertyName("name")
        public String getName();
        
        @PropertyName("excerpt")
        public String getExcerpt();
    }

    interface ForumCollection {
        String getTotalCount();
        List<Topic> getTopics();
    }

    interface TopicLoadConfig extends PagingLoadConfig {
        String getQuery();

        void setQuery(String query);

        @Override
        @PropertyName("start")
        public int getOffset();

        @Override
        @PropertyName("start")
        public void setOffset(int offset);
    }

    interface ForumListLoadResult extends PagingLoadResult<Topic> {
        void setData(List<Topic> data);

        @Override
        @PropertyName("start")
        public int getOffset();

        @Override
        @PropertyName("start")
        public void setOffset(int offset);
    }

    interface ForumProperties extends PropertyAccess<Topic> {
        ModelKeyProvider<Topic> file();
        LabelProvider<Topic> name();
    }

    private ComboBox<Topic> combo;
    private FramedPanel vp;

    public Widget asWidget() {

        if (vp == null) {
            vp = new FramedPanel();
            vp.setHeaderVisible(false);

            String url = "/assets/util/search_topics.jsp";
            // String url = "http://www.sencha.com/forum/topics-remote.php";

            ScriptTagProxy<TopicLoadConfig> proxy = new ScriptTagProxy<TopicLoadConfig>(url);
            proxy.setWriter(new UrlEncodingWriter<TopicLoadConfig>(TopicAutoBeanFactory.instance, TopicLoadConfig.class));

            JsoReader<ForumListLoadResult, ForumCollection> reader = new JsoReader<ForumListLoadResult, ForumCollection>(TopicAutoBeanFactory.instance, ForumCollection.class) {
                @Override
                protected ForumListLoadResult createReturnData(Object loadConfig, ForumCollection records) {
                    PagingLoadConfig cfg = (PagingLoadConfig) loadConfig;
                    ForumListLoadResult res = TopicAutoBeanFactory.instance.dataLoadResult().as();
                    res.setData(records.getTopics());
                    res.setOffset(cfg.getOffset());
                    res.setTotalLength(Integer.parseInt(records.getTotalCount()));
                    return res;
                }
            };

            PagingLoader<TopicLoadConfig, ForumListLoadResult> loader = new PagingLoader<TopicLoadConfig, ForumListLoadResult>(proxy, reader);
            loader.useLoadConfig(TopicAutoBeanFactory.instance.loadConfig().as());
            loader.addBeforeLoadHandler(new BeforeLoadHandler<TopicLoadConfig>() {
                @Override
                public void onBeforeLoad(BeforeLoadEvent<TopicLoadConfig> event) {
                    String query = combo.getText();
                    if (query != null && !query.equals("")) {
                        event.getLoadConfig().setQuery(query);
                    }
                }
            });

            ForumProperties props = GWT.create(ForumProperties.class);

            ListStore<Topic> store = new ListStore<Topic>(props.file());
            loader.addLoadHandler(new LoadResultListStoreBinding<TopicLoadConfig, Topic, ForumListLoadResult>(store));

            final Bundle b = GWT.create(Bundle.class);
            b.css().ensureInjected();

            final ExampleTemplate template = GWT.create(ExampleTemplate.class);

            ListView<Topic, Topic> view = new ListView<Topic, Topic>(store, new IdentityValueProvider<Topic>());
            view.setCell(new AbstractCell<Topic>() {
                @Override
                public void render(com.google.gwt.cell.client.Cell.Context context, Topic value, SafeHtmlBuilder sb) {
                    sb.append(template.render(value, b.css()));
                }
            });

            ComboBoxCell<Topic> cell = new ComboBoxCell<Topic>(store, props.name(), view);

            combo = new ComboBox<Topic>(cell);
            combo.setLoader(loader);
            combo.setHideTrigger(true);
            combo.setWidth(500);
            combo.setPageSize(100);
            combo.setEmptyText("Enter Topic");
            combo.addBeforeSelectionHandler(new BeforeSelectionHandler<Topic>() {
                @Override
                public void onBeforeSelection(BeforeSelectionEvent<Topic> event) {
                    event.cancel();
                    Topic f = combo.getListView().getSelectionModel().getSelectedItem();
                    Info.display("Exploring Topic", f.getName());
                    
                    TopicExplorerManager.getInstance().exploreTopic(new hotmath.gwt.cm_rpc.client.model.Topic(f.getName(), f.getFile(), f.getExcerpt()));
                    
                    if(window != null) {
                        window.hide();
                        window.removeFromParent();
                    }
                }
            });

            combo.getElement().getStyle().setMargin(10, Unit.PX);

            FlowLayoutContainer flow = new FlowLayoutContainer();
            flow.add(new HTML(
                    "<p style='text-align: center;font-style: italic'>Enter the topic you would like more help with...</p>"));
            flow.add(combo);

            vp.setWidget(flow);
        }

        return vp;
    }

    public static void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                new SearchPanel().showWindow();
            }
        });
    }

}