package hotmath.gwt.cm_tools.client.ui.search;

import hotmath.gwt.cm_tools.client.util.CmMessageBox;

import com.google.gwt.core.shared.GWT;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Button that manages enable/disabled stat manually
 *  to allow disabled tooltips.
 *  
 * @author casey
 *
 */
public class SearchButton extends TextButton {

    
    
    final String _disabledTooltip = "Search (currently disabled) lets you view Catchup Math lessons including videos, practice problems, activities and more.";
    final String _enabledTooltip = "Search lets you view Catchup Math lessons including practice problems, activities and videos.";
    
    
    MyResources resources = GWT.create(MyResources.class);
    
    static public enum ButtonState{ENABLED, DISABLED};
    ButtonState _state;
    
    static SearchButton __lastInstance;
    public SearchButton() {
        super();
        __lastInstance = this;
        addStyleName("cm-search-btn");
        
        _state = ButtonState.ENABLED;
        setState(_state);
        addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(_state == ButtonState.DISABLED) {
                    CmMessageBox.showAlert("Disabled",  "Lesson search is currently disabled");
                }
                else {
                    TopicExplorerManager.getInstance().showSearch();
                }
            }
        });
    }
    
    
    @Override
    public void setEnabled(boolean enabled) {
        if(enabled) {
            setState(ButtonState.ENABLED);
        }
        else {
            setState(ButtonState.DISABLED);
        }
    }
    
    public void setState(ButtonState state) {
        _state = state;
        if(state == ButtonState.DISABLED) {
            setIcon(resources.searchDisabled());
            setToolTip(_disabledTooltip);
        }
        else {
            setIcon(resources.searchEnabled());
            setToolTip(_enabledTooltip);
        }
    }
}
