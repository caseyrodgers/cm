package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_core.client.model.SearchSuggestion;
import hotmath.gwt.cm_tools.client.ui.search.SearchPanel.SeachSuggestionChoosen;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Popup;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class SearchSuggestionPopup extends Popup {

    private ListView<SearchSuggestion, String> _view;
    private SeachSuggestionChoosen callback;
    public SearchSuggestionPopup(List<SearchSuggestion> suggestions, final SeachSuggestionChoosen seachSuggestionChoosen) {
        
        
        this.callback = seachSuggestionChoosen;
        setPixelSize(150,  200);
        setAutoHide(true);
        
        FramedPanel frame = new FramedPanel();

        ListStore<SearchSuggestion> store = new ListStore<SearchSuggestion>(props.key());
        _view = new ListView<SearchSuggestion, String>(store, props.suggestion());
        
        _view.addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                doSelect();
            }
        }, DoubleClickEvent.getType());

        store.addAll(suggestions);
        
        
        
        BorderLayoutContainer blc = new BorderLayoutContainer();

        blc.setCenterWidget(_view);
        
        frame.setWidget(_view);
        frame.setHeadingText("Search Suggestions");
        
        frame.addButton(new TextButton("Select", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSelect();
            }
        }));

        //blc.setSouthWidget(new TextButton("Select"), new BorderLayoutData(35));

        
       
        setWidget(frame);
    }

    protected void doSelect() {
        SearchSuggestion selected =_view.getSelectionModel().getSelectedItem();
        callback.choosen(selected);
        hide();
    }

    interface Props extends PropertyAccess<String> {
        ValueProvider<SearchSuggestion, String> suggestion();

        @Path("suggestion")
        ModelKeyProvider<SearchSuggestion> key();
    }
    static Props props = GWT.create(Props.class);

}
