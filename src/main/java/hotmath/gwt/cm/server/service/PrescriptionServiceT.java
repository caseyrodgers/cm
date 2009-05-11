package hotmath.gwt.cm.server.service;

import hotmath.gwt.cm.client.service.PrescriptionService;
import hotmath.gwt.cm.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PrescriptionServiceT extends RemoteServiceServlet implements PrescriptionService {

    public RpcData createTestRun(int testId) throws CmRpcException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getHmContent(String file, String fileBase) {
        // TODO Auto-generated method stub
        return null;
    }

    public RpcData getPrescriptionSessionJson(int runId, int sessionNumber, boolean updateActiveInfo)
            throws CmRpcException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayList<RpcData> getQuizCurrentResults(int testId) throws CmRpcException {
        // TODO Auto-generated method stub
        return null;
    }

    public RpcData getQuizHtml(int uid, int testSegment) {
        // TODO Auto-generated method stub
        return null;
    }

    public RpcData getQuizHtml(int testId) throws CmRpcException {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSolutionHtml(String pid) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSolutionProblemStatementHtml(String pid) {
        // TODO Auto-generated method stub
        return null;
    }

    public RpcData getUserInfo(int uid) throws CmRpcException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayList<RpcData> getViewedInmhItems(int runId) throws CmRpcException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayList<RpcData> getWhiteboardData(int uid, String pid) throws CmRpcException {
        // TODO Auto-generated method stub
        return null;
    }

    public void saveQuizCurrentResult(int testId, boolean isCorrect, int answerIndex, String pid) throws CmRpcException {
        // TODO Auto-generated method stub
        
    }

    public void saveWhiteboardData(int uid, String pid, String command, String commandData) throws CmRpcException {
        // TODO Auto-generated method stub
        
    }

    public void setInmhItemAsViewed(int runId, String type, String file) throws CmRpcException {
        // TODO Auto-generated method stub
        
    }
}