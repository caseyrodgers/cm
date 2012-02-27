package hotmath.gwt.cm_search.client.view;


import hotmath.gwt.cm_mobile_shared.client.event.DoSearchEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.GenericContainerTag;
import hotmath.gwt.cm_mobile_shared.client.util.GenericTextTag;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent;
import hotmath.gwt.cm_mobile_shared.client.util.TouchClickEvent.TouchClickHandler;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_search.client.CatchupMathSearch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchViewImpl extends Composite implements SearchView {

    interface SearchViewImplUiBinder extends    UiBinder<Widget, SearchViewImpl> { }
    private static SearchViewImplUiBinder uiBinder = GWT.create(SearchViewImplUiBinder.class);
    Presenter presenter;

    GenericContainerTag listItems = new GenericContainerTag("ul");
    
    public SearchViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
        
        listItems.addStyleName("touch");
        listItems.addStyleName("large");
        listItemsDiv.add(listItems);
    }

    @Override
    public void showSearch() {
    }
    
    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("searchButton")
    protected void handleSearchButton(ClickEvent ce) {
        
        
        String val = searchField.getValue();
        if(val == null || val.length() < 3) {
            MessageBox.showError("Invalid search term");
            return;
        }
        
        CatchupMathSearch.__clientFactory.getEventBus().fireEvent(new DoSearchEvent(val));
        
//        presenter.doSearch(searchField.getText(), new CallBack() {
//            @Override
//            public void searchResults(CmList<Topic> results) {
//                showResults(results);
//            }
//        });
    }

    @Override
    public void showSearchResults(String searchedFor, CmList<Topic> topics) {
        searchField.setValue(searchedFor);
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
