package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile3.client.activity.SearchActivity.CallBack;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchViewImpl extends AbstractPagePanel implements SearchView {

    Presenter presenter;

    private static SearchViewImplUiBinder uiBinder = GWT.create(SearchViewImplUiBinder.class);

    interface SearchViewImplUiBinder extends UiBinder<Widget, SearchViewImpl> {
    }

    GenericContainerTag listItems = new GenericContainerTag("ul");

    public SearchViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));

        listItems.addStyleName("touch");
        listItems.addStyleName("large");
        
        listItemsDiv.add(listItems);
    }

    
    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }
    
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getBackButtonText() {
        return (SharedData.getUserInfo() != null && SharedData.getUserInfo().getUid() != 0)?"Back":null;
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }

    @Override
    public String getViewTitle() {
        return "Search Catchup Math";
    }
    
    
    @Override
    public String getHeaderBackground() {
        return "#7F2909"; 
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                return true;
            }
        };
    }

    @UiHandler("searchButton")
    protected void handleSearchButton(ClickEvent ce) {
        presenter.doSearch(searchField.getText(), new CallBack() {
            @Override
            public void searchResults(CmList<TopicMatch> results) {
                showResults(results);
            }
        });
    }

    private void showResults(CmList<TopicMatch> topics) {
        listItems.clear();
        
        if(topics.size() == 0) {
            listItems.add(new GenericTextTag<String>("h1", "Search term not found, try again."));
        }
        else {
            for (TopicMatch topicMatch : topics) {
    
                Topic topic = topicMatch.getTopic();
                GenericTextTag<String> tt = new MyGenericTextTag(topic);
                tt.addStyleName("group");
                tt.addHandler(new TouchClickHandler<String>() {
                    @Override
                    public void touchClick(TouchClickEvent<String> event) {
                        presenter.loadTopic( ((MyGenericTextTag)event.getSource()).getTopic().getFile());
                    }
                });
    
                listItems.add(tt);
            }
        }
    }

    @UiHandler("searchField")
    protected void doChangeValue(ValueChangeEvent<String> event) {
        handleSearchButton(null);
    }

    @UiField
    Button searchButton;

    @UiField
    TextBox searchField;

    @UiField
    SimplePanel listItemsDiv;

    class MyGenericTextTag extends GenericTextTag<String> {
        Topic topic;

        public MyGenericTextTag(Topic topic) {
            super("li");
            this.topic = topic;
            
            setText(topic.getName());
        }
        
        public Topic getTopic() {
            return topic;
        }
    }

}
