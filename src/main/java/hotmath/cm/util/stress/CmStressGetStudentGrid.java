package hotmath.cm.util.stress;

import java.util.Collection;
import java.util.Map;

import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.SortInfo;

public class CmStressGetStudentGrid implements StressTest {
    public CmStressGetStudentGrid() {

    }

    @Override
    public void runTest(int aid, int uid, String uName, String uPass) throws Exception {
        PagingLoadConfig pConfig = new PagingLoadConfig() {

            @Override
            public SortDir getSortDir() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getSortField() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public SortInfo getSortInfo() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setSortDir(SortDir sortDir) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void setSortField(String sortField) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void setSortInfo(SortInfo info) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public <X> X get(String property) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Map<String, Object> getProperties() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Collection<String> getPropertyNames() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <X> X remove(String property) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <X> X set(String property, X value) {
                // TODO Auto-generated method stub
                return null;
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
               
        };
        
        GetStudentGridPageAction pageAction = new GetStudentGridPageAction(aid,pConfig);
        ActionDispatcher.getInstance().execute(pageAction);
    }

}
