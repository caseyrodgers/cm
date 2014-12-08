package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.StudentSearchInfo;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Create panel to show a quick search text field and clear button
 * 
 * @author casey
 * 
 */
class QuickSearchPanel extends HorizontalPanel {
    TextField quickFilter;

    public QuickSearchPanel() {

        String qfTip = "Apply text filter to first four columns.";
        quickFilter = new TextField();
        quickFilter.setEmptyText("--- Text Search ---");
        quickFilter.setWidth("210px");
        quickFilter.setToolTip(qfTip);
        //quickFilter.setFieldLabel("Text Search");
        
        quickFilter.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                applyQuickSearch();
            }
        });

        add(quickFilter);

        TextButton submit = new TextButton("submit", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                applyQuickSearch();
            }
        });
        submit.setToolTip(qfTip);
        add(submit);
        TextButton clear = new TextButton("clear", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                quickFilter.setValue("");
                boolean shouldRefresh = (StudentSearchInfo.__instance.getQuickSearch() != null && StudentSearchInfo.__instance.getQuickSearch().length() > 0);
                StudentSearchInfo.__instance.setQuickSearch(null);
                if (shouldRefresh)
                    StudentGridPanel.instance.loadAndResetStudentLoader();
            }
        });
        clear.setToolTip("Clear text");
        add(clear);
    }

    private void applyQuickSearch() {
        CmLogger.debug("GroupFilter: setting quick search: " + quickFilter.getValue());
        boolean shouldRefresh = true;
        if (StudentSearchInfo.__instance.getQuickSearch() != null && StudentSearchInfo.__instance.getQuickSearch().equals(quickFilter.getValue()))
            shouldRefresh = false;

        if (shouldRefresh) {
            StudentSearchInfo.__instance.setQuickSearch(quickFilter.getCurrentValue());
            StudentGridPanel.instance.loadAndResetStudentLoader();
        }
    }
}
