package hotmath.gwt.cm_mobile3.client.view;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.activity.SearchActivity.CallBack;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewEvent;
import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.rpc.CmList;

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
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getBackButtonText() {
        return "Login";
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
    public String getTitle() {
        return "Search Catchup Math";
    }

    @Override
    public BackAction getBackAction() {
        return new BackAction() {
            @Override
            public boolean goBack() {
                CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new ShowLoginViewEvent());
                return false;
            }
        };
    }

    @UiHandler("searchButton")
    protected void handleSearchButton(ClickEvent ce) {
        presenter.doSearch(searchField.getText(), new CallBack() {
            @Override
            public void searchResults(CmList<Topic> results) {
                showResults(results);
            }
        });
    }

    private void showResults(CmList<Topic> topics) {
        listItems.clear();
        for (Topic topic : topics) {

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
