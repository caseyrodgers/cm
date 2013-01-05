package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.shared.client.CmShared;
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

    private void loadStandards(CmList<String> standards) {

        ListStore<StandardsModel> store = new ListStore<StandardsModel>(__props.id());
        ListView<StandardsModel, String> _listView = new ListView<StandardsModel, String>(store, __props.standard());
        for (String standard : standards) {
            StandardsModel lm = new StandardsModel(standard);
            store.add(lm);
        }
        setWidget(_listView);
        forceLayout();
    }

    /**
     * Call RPC to get list of standards, then display in modal window over
     * current
     * 
     * @param lim
     */
    private void readStateStandards(final String topic, final String state) {
        new RetryAction<CmList<String>>() {
            @Override
            public void attempt() {
                GetStateStandardsAction action = new GetStateStandardsAction(topic, state);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<String> result) {
                loadStandards(result);
                CatchupMathTools.setBusy(false);
            }
        }.attempt();
    }

    interface StandardModelProperties extends PropertyAccess<String> {
        @Path("standard")
        ModelKeyProvider<StandardsModel> id();
        ValueProvider<StandardsModel, String> standard();
    }
    class StandardsModel implements Response {
        String standard;
        public StandardsModel(){}
        
        public StandardsModel(String standard) {
            this.standard = standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getStandard() {
            return standard;
        }
    }
}