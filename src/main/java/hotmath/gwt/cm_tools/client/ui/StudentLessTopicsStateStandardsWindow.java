package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetStateStandardsAction;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StudentLessTopicsStateStandardsWindow extends Window {
    
    String topic;
    public StudentLessTopicsStateStandardsWindow(LessonItemModel lessonModel) {
           readCaliStateStandards(lessonModel.getFile());
         
            setModal(true);
            setSize(325, 250);
            setHeading("California State Standards for: " + lessonModel.getName());

            setLayout(new FitLayout());

            Button close = new Button("Close");
            close.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    close();
                }
            });
            addButton(close);
            
            
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
    private void readCaliStateStandards(final String topic) {
        CatchupMathTools.setBusy(true);
        
        CmServiceAsync s = CmShared.getCmService();
        s.execute(new GetStateStandardsAction(topic), new AsyncCallback<CmList<String>>() {
            public void onSuccess(CmList<String> result) {
                loadStandards(result);
                CatchupMathTools.setBusy(false);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error reading california standareds", caught);
                CatchupMathTools.setBusy(false);
            }
        });
    }    
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
