package hotmath.cm.util.stress;

import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

import java.util.List;

import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;



public class CmStressGetStudentGrid implements StressTest {
    public CmStressGetStudentGrid() {

    }

    @Override
    public void runTest(int aid, int uid, String uName, String uPass) throws Exception {
        PagingLoadConfig pConfig = new MyPagingLoadConfig();
        GetStudentGridPageAction pageAction = new GetStudentGridPageAction(aid,pConfig);
        ActionDispatcher.getInstance().execute(pageAction);
    }
    
    class MyPagingLoadConfig implements PagingLoadConfig {

        @Override
        public List<? extends SortInfo> getSortInfo() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setSortInfo(List<? extends SortInfo> info) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setLimit(int limit) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setOffset(int offset) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public int getLimit() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getOffset() {
            // TODO Auto-generated method stub
            return 0;
        }
    }

}
