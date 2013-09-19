package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.StateStandard;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;


public class StudentLessTopicsStateStandardsWindow extends GWindow {

    String topic;
    
    static StandardModelProperties __props = GWT.create(StandardModelProperties.class);

    public StudentLessTopicsStateStandardsWindow(LessonItemModel lessonModel, String stateLabel, String state) {
        super(true);
        readStateStandards(lessonModel.getFile(), state);

        setModal(true);
        setPixelSize(375, 250);
        if (state.length() == 2)
            setHeadingText(stateLabel + " State Standards for: " + lessonModel.getName());
        else {
            setHeadingText("Common Core Standards for: " + lessonModel.getName());
        }
        setVisible(true);
    }

    private void loadStandards(CmList<StateStandard> standards) {

        ListStore<UiStandardsModel> store = new ListStore<UiStandardsModel>(__props.id());
        ListView<UiStandardsModel, String> _listView = new ListView<UiStandardsModel, String>(store, __props.standard());
        for (StateStandard standard : standards) {
            if(isNullOrBlank(standard.getStandardNameNew()) == false) {
                store.add(new UiStandardsModel(standard.getTopic(),standard.getStandardNameNew(), Type.NEW_STANDARD));
                continue;
            }

            if(isNullOrBlank(standard.getStandardName()) == false) {
                store.add(new UiStandardsModel(standard.getTopic(),standard.getStandardName(), Type.OLD_STANDARD));
            }
            
        }
        setWidget(_listView);
        forceLayout();
    }

    private boolean isNullOrBlank(String name) {
    	return (name == null || name.trim().length() == 0);
    }

    /**
     * Call RPC to get list of standards, then display in modal window over
     * current
     * 
     * @param lim
     */
    private void readStateStandards(final String topic, final String state) {
        new RetryAction<CmList<StateStandard>>() {
            @Override
            public void attempt() {
                GetStateStandardsAction action = new GetStateStandardsAction(topic, state);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<StateStandard> result) {
                loadStandards(result);
                CatchupMathTools.setBusy(false);
            }
        }.attempt();
    }

    interface StandardModelProperties extends PropertyAccess<String> {
        @Path("standard")
        ModelKeyProvider<UiStandardsModel> id();
        ValueProvider<UiStandardsModel, String> standard();
    }
    
    enum Type{OLD_STANDARD, NEW_STANDARD};
    
    class UiStandardsModel implements Response {
        String standard;
        String topic;
        Type type;
        public UiStandardsModel(){}
        
        public UiStandardsModel(String topic, String standard, Type type) {
            this.topic = topic;
            this.standard = standard;
            this.type = type;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getStandard() {
            return standard;
        }
    }
}