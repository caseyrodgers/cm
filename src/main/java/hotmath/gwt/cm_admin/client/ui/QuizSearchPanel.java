package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.ui.CmLogger;

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
        quickFilter.setWidth("200px");
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
                boolean shouldRefresh = (StudentGridPanel.instance.__searchInfo.getQuickSearch() != null && StudentGridPanel.instance.__searchInfo.getQuickSearch().length() > 0);
                StudentGridPanel.instance.__searchInfo.setQuickSearch(null);
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
        if (StudentGridPanel.instance.__searchInfo.getQuickSearch() != null && StudentGridPanel.instance.__searchInfo.getQuickSearch().equals(quickFilter.getValue()))
            shouldRefresh = false;

        if (shouldRefresh) {
            StudentGridPanel.instance.__searchInfo.setQuickSearch(quickFilter.getValue());
            StudentGridPanel.instance.loadAndResetStudentLoader();
        }
    }
}
