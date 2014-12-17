package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.Topic;
import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc.client.model.TopicMatch.MatchWeight;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.search.HMIndexSearcher;
import hotmath.search.HMIndexWriter;
import hotmath.search.HMIndexWriterFactory;
import hotmath.search.Hit;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.sdicons.json.validator.impl.predicates.Array;


/**
 * 
 * Search for a lesson/topic using wildcards
 * 
 * Returns a list of topics that match search.
 * 
 * User can select a given lesson (multi?) and have
 * a prescription created based on that lesson.
 * 
 * 
 * @author casey
 * 
 */
public class SearchTopicCommand implements ActionHandler<SearchTopicAction, CmList<TopicMatch>> {

    static Logger __logger = Logger.getLogger(SearchTopicCommand.class);

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SearchTopicAction.class;
    }

    @Override
    public CmList<TopicMatch> execute(Connection conn, SearchTopicAction action) throws Exception {
        List<Topic> topics = new ArrayList<Topic>();
        try {
            Hit[] results = HMIndexSearcher.getInstance().searchFor("inmh", action.getSearch());
            for (Hit hit : results) {
                String url =  hit.getUrl();
                if(url == null) {
                    url = hit.getName();
                }
                String title = hit.getTitle();
                title = title.replace("\n",  "").trim();
                title = StringEscapeUtils.unescapeHtml(title);
                topics.add(new Topic(title,url, hit.getSummary()));
            }
        
            return getOrderedTopics(makeSureOnlyExplorableTopicsIncluded(conn, topics), action.getSearch());
            
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
        
    }
    
    private  List<Topic> makeSureOnlyExplorableTopicsIncluded(Connection conn, List<Topic> topics) throws Exception {
        List<Topic> titles = new ArrayList<Topic>();
        PreparedStatement ps=null;
        try {
            String sql = "select * from inmh_assessment where file = ?";
            ps = conn.prepareStatement(sql);
            for(Topic t: topics) {
                ps.setString(1,  t.getFile());
                ResultSet rs = ps.executeQuery();
                if(rs.first()) {
                   titles.add(t);   
                }
            }
            
            return titles;
        }
        finally {
            SqlUtilities.releaseResources(null,  ps, null);
        }
    }

    private CmList<TopicMatch> getOrderedTopics(List<Topic> topics, String search) {
        
        // find entries that match exactly and move to top        
        CmList<TopicMatch> ordered = new CmArrayList<TopicMatch>();
        for(int i=topics.size()-1;i>-1;i--) {
            Topic t = topics.get(i);
            MatchWeight matchWeight = determineMatchWeight(t, search);
            if(matchWeight != null) {
                ordered.add(new TopicMatch(t,matchWeight));
            }
        }
        
        // first sort entire list
        Collections.sort(ordered, new Comparator<TopicMatch>() {
            @Override
            public int compare(TopicMatch o1, TopicMatch o2) {
                String t1 = o1 != null?o1.getTopicName():null;
                String t2 = o2 != null?o2.getTopicName():null;
                if(t1 == null) {
                    t1 = "";
                }
                if(t2 == null) {
                    t2 = "";
                }
                t1 = t1.toLowerCase();
                t2 = t2.toLowerCase();
                
                
                MatchWeight m1 = o1.getMatchWeight();
                MatchWeight m2 = o2.getMatchWeight();
                
                t1 = getMatchWeightPadding(m1) + t1;
                t2 = getMatchWeightPadding(m2) + t2;
                
                return t1.compareTo(t2);
            }
        });        
        return ordered;
    }
    
    

    private String getMatchWeightPadding(MatchWeight m1) {
        int numSpaces = m1.ordinal()+1;
        String padding="";
        for(int i=0;i<numSpaces;i++) {
            padding += " ";
        }
        return padding;
    }

    /** SEE CM-996
     * 
     * @param t
     * @param search
     * @return
     */
    private MatchWeight determineMatchWeight(Topic t, String search) {
        if(_checkTitleMatchesAbsolute(t, search)) {
            return MatchWeight.TITLE_MATCH_ABSOLUTE;
        }
        else if(_checkTitleMatchesAbsolutePlus(t, search)) {
            return MatchWeight.TITLE_MATCH_ALL_PLUS;
        }
        else if(_checkContentContainsAll(t, search)) {
            return MatchWeight.CONTENT_MATCH_ALL;
        }
        else if(_checkTitleContainsSome(t, search)) {
            return MatchWeight.TITLE_MATCH_SOME;
        }
        else if(_checkContentContainsSome(t, search)) {
            return MatchWeight.CONTENT_MATCH_SOME;
        }
        return null;
    }

    private boolean _checkContentContainsSome(Topic topic, String search) {
        String t = topic.getExcerpt();
        if(containsSomeSearchTerms(t, search)) {
            return true;
        }
        return false;
    }

    private boolean _checkTitleContainsSome(Topic topic, String search) {
        String t = topic.getName();
        
        if(containsSomeSearchTerms(t, search)) {
            return true;
        }
        return false;
    }

    private boolean _checkContentContainsAll(Topic t, String search) {
        String content = t.getExcerpt();
        
        if(containsAllSearchTerms(content,  search)) {
            return true;
        }
        else {
            return false;
        }
    }

    private boolean _checkTitleMatchesAbsolute(Topic topic, String search) {
        String t = topic.getName();
        if(containsAllSearchTerms(t, search) && t.length() == search.length()) {
            return true;
        }
        return false;
    }

    
    private boolean _checkTitleMatchesAbsolutePlus(Topic topic, String search) {
        String t = topic.getName();
        if(containsAllSearchTerms(t, search)) {
            return true;
        }
        return false;
    }
    
    private boolean containsAllSearchTerms(String name, String search) {
        String words[] = search.toLowerCase().split(" ");
        name = name.toLowerCase();
        for(String w: words) {
            if(!name.contains(w)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsSomeSearchTerms(String name, String search) {
        String words[] = search.toLowerCase().split(" ");
        name = name.toLowerCase();
        for(String w: words) {
            if(name.contains(w)) {
                return true;
            }
        }
        return false;
    }
    
    static public void main(String as[]) {
        try {
            HMIndexWriter hmIw = HMIndexWriterFactory.getHMIndexWriter("inmh");
            hmIw.writeIndex();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
