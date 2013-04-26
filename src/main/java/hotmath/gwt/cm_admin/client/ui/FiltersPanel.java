package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.GroupSelectorWidget;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.StudentSearchInfo;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class FiltersPanel implements IsWidget, ProcessTracker {

    ListStore<GroupInfoModel> groupStore;

    GroupProperties groupProps = GWT.create(GroupProperties.class);

    private CmAdminModel cmAdminModel;
    
    HorizontalLayoutContainer _mainHorCont;
    
    static public String quickSearch;
    public FiltersPanel(CmAdminModel cmAdminModel, DateRangePanel dateRangePanel) {
        this.cmAdminModel = cmAdminModel;
        
        groupStore = new ListStore<GroupInfoModel>(groupProps.id());
        
        
        GroupSelectorWidget gsw = new GroupSelectorWidget(cmAdminModel, groupStore, false, this, "group-filter", false, groupProps.groupName(), null);
        ComboBox<GroupInfoModel> groupCombo = gsw.groupCombo();
        groupCombo.setAllowBlank(true);

        groupCombo.addSelectionHandler(new SelectionHandler<GroupInfoModel>() {

            @Override
            public void onSelection(SelectionEvent<GroupInfoModel> event) {
                // filter grid based on current selection
                GroupInfoModel gm = event.getSelectedItem();
                StudentSearchInfo.__instance.setGroupIdFilter(gm.getId());

                StudentGridPanel.instance.loadAndResetStudentLoader();
            }
        });

        _mainHorCont = new HorizontalLayoutContainer();

        class MyFormPanel extends SimpleContainer {
            VerticalLayoutContainer vPane = new VerticalLayoutContainer();
            MyFormPanel() {
                addStyleName("filters-my-form-panel");
                setWidth(310);
                setWidget(vPane);
            }

            @Override
            public void add(IsWidget child) {
                vPane.add(child);
            }
        }

        MyFormPanel groupForm = new MyFormPanel();
        groupForm.add(new MyFieldLabel(groupCombo, "Group", 50));
        _mainHorCont.add(groupForm);

        MyFormPanel quickSearchForm = new MyFormPanel();
        quickSearchForm.add(new QuickSearchPanel());
        
        _mainHorCont.add(quickSearchForm);

        // Date Range Panel

        MyFormPanel dateRangeForm = new MyFormPanel();
        //dateRangeForm.setWidth(300);
        // fp.setLabelWidth(80);
        dateRangeForm.add(dateRangePanel);
        
        _mainHorCont.add(dateRangeForm);

    }

    @Override
    public Widget asWidget() {
        return _mainHorCont;
    }

    @Override
    public void beginStep() {
    }

    @Override
    public void completeStep() {
    }

    @Override
    public void finish() {
    }

    public interface GroupProperties extends PropertyAccess<String> {
        ModelKeyProvider<GroupInfoModel> id();
        LabelProvider<GroupInfoModel> groupName();
    }
}
