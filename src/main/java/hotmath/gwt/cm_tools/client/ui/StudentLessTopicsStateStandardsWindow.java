package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class StudentLessTopicsStateStandardsWindow extends CmWindow {
    
    String topic;
    public StudentLessTopicsStateStandardsWindow(LessonItemModel lessonModel,String stateLabel, String state) {
        
            readStateStandards(lessonModel.getFile(),state);
         
            setModal(true);
            setSize(325, 250);
            if(state.length() == 2)
            	setHeading(stateLabel + " State Standards for: " + lessonModel.getName());
            else {
            	setHeading("Common Core Standards for: " + lessonModel.getName());
            }
            setLayout(new FitLayout());
            addCloseButton();
            setVisible(true);        
        }
    
    
    private void loadStandards(CmList<String> standards) {
        
        ListView<StandardsModel> _listView = new ListView<StandardsModel>();
        _listView.setSimpleTemplate("<div>{standard}</div>");
        ListStore<StandardsModel> store = new ListStore<StandardsModel>();

        for (String standard : standards) {
            StandardsModel lm = new StandardsModel(standard);
            store.add(lm);
        }
        
        _listView.setStore(store);        
        
        add(_listView);
        
        layout();
    }
    

    /**
     * Call RPC to get list of standards, then display in modal window over
     * current
     * 
     * @param lim
     */
    private void readStateStandards(final String topic,final String state) {
        new RetryAction<CmList<String>>() {
            @Override
            public void attempt() {
                GetStateStandardsAction action = new GetStateStandardsAction(topic,state);
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

    class StandardsModel extends BaseModel {

        public StandardsModel(String standard) {
            setStandard(standard);
        }

        public void setStandard(String standard) {
            set("standard", standard);
        }

        public String getStandard() {
            return get("standard");
        }
    }        
}