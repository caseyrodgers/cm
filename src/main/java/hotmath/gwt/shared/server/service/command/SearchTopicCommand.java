package hotmath.gwt.shared.server.service.command;

import hotmath.ProblemID;
import hotmath.flusher.Flushable;
import hotmath.flusher.HotmathFlusher;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

/**
 *
 * Search for a lesson/topic using wildcards
 *
 * Returns a list of topics that match search.
 *
 * User can select a given lesson (multi?) and have a prescription created based
 * on that lesson.
 *
 *
 * @author casey
 *
 */
public class SearchTopicCommand implements ActionHandler<SearchTopicAction, CmList<TopicMatch>> {

    static Logger __logger = Logger.getLogger(SearchTopicCommand.class);
    static Map<String, Integer> __rankings;

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return SearchTopicAction.class;
    }

    static {
        HotmathFlusher.getInstance().addFlushable(new Flushable() {
            public void flush() {
                __rankings = null;
            }
        });
    }

    class MyListSet extends ArrayList<Topic> {
        
        @Override
        public boolean add(Topic element) {
            if(!contains(element)) {
                return super.add(element);
            }
            else {
                return false;
            }
        }
    }
    @Override
    public CmList<TopicMatch> execute(Connection conn, SearchTopicAction action) throws Exception {

        if (__rankings == null) {
            __rankings = __readLessonRankings(conn);
        }

        List<Topic> topics = new MyListSet();
        try {
            Hit[] resultsInmh = HMIndexSearcher.getInstance().searchFor("inmh", action.getSearch());
            for (Hit hit : resultsInmh) {
                String url = hit.getUrl();
                if (url == null) {
                    url = hit.getName();
                }
                String title = hit.getTitle();
                title = title.replace("\n", "").trim();
                title = StringEscapeUtils.unescapeHtml(title);
                topics.add(new Topic(title, url, hit.getSummary()));
            }

//            Hit[] resultsSolutions = HMIndexSearcher.getInstance().searchFor("solutions", action.getSearch());
//            for (Hit hit : resultsSolutions) {
//                String url = hit.getUrl();
//                if (url == null) {
//                    url = hit.getName();
//                }
//
//                String pid = hit.getName();
//
//                Topic pidTopic = findTopicAssoicatedWithPid(conn, pid);
//                if (pidTopic != null) {
//                    topics.add(pidTopic);
//                }
//            }

            // topics.addAll(doSimpleTextSearch(action.getSearch()));

            return getOrderedTopics(makeSureOnlyExplorableTopicsIncluded(conn, topics), action.getSearch());

        } catch (Throwable e) {
            __logger.error("Error occurred executing search", e);
            throw e;
        }

    }

    private Topic findTopicAssoicatedWithPid(Connection conn, String pid) throws Exception {

        String sql = "select * from inmh_map where guid = ? limit 1";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, pid);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                String file = rs.getString("file");
                String title = rs.getString("title");
                return new Topic(title, file, pid);
            } else {
                return null;
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }

    }

    private Collection<? extends Topic> doSimpleTextSearch(String search) throws Exception {

        List<Topic> topics = new ArrayList<Topic>();
        IndexReader reader = HMIndexSearcher.getInstance().getIndexReader("inmh");

        for (int i = 0; i < reader.maxDoc(); i++) {
            if (reader.isDeleted(i)) {
                continue;
            }

            Document doc = reader.document(i);
            String content = doc.get("summary");
            String title = doc.get("title");
            String file = doc.get("name");
            if (file == null || content == null) {
                continue;
            }

            if (content.toLowerCase().indexOf(search) > -1 || title.toLowerCase().indexOf(search) > -1) {

                int t = file.indexOf("topics/");
                if (t > -1) {
                    String url = file.substring(t);
                    topics.add(new Topic(title, url, content));
                }
            }

        }

        return topics;

    }

    private Map<String, Integer> __readLessonRankings(Connection conn) throws Exception {
        Map<String, Integer> map = new HashMap<String, Integer>();
        PreparedStatement ps = null;
        try {
            String sql = "select * from HA_LESSON_RANK";
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("lesson_file"), rs.getInt("rank"));
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return map;
    }

    /** Only include topics that have assigned RPPS (inmh_assessment)
     * 
     * @param conn
     * @param topics
     * @return
     * @throws Exception
     */
    private List<Topic> makeSureOnlyExplorableTopicsIncluded(Connection conn, List<Topic> topics) throws Exception {
        List<Topic> titles = new ArrayList<Topic>();
        PreparedStatement ps = null;
        try {
            String sql = "select * from inmh_assessment where file = ?";
            ps = conn.prepareStatement(sql);
            for (Topic t : topics) {
                ps.setString(1, t.getFile());
                ResultSet rs = ps.executeQuery();
                if (rs.first()) {
                    titles.add(t);
                }
            }

            return titles;
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    private CmList<TopicMatch> getOrderedTopics(List<Topic> topics, String search) {

        // find entries that match exactly and move to top
        CmList<TopicMatch> ordered = new CmArrayList<TopicMatch>();
        for (int i = topics.size() - 1; i > -1; i--) {
            Topic t = topics.get(i);
            MatchWeight matchWeight = determineMatchWeight(t, search);
            if (matchWeight != null) {
                ordered.add(new TopicMatch(t, matchWeight));
            }
        }

        // first sort entire list
        Collections.sort(ordered, new Comparator<TopicMatch>() {
            @Override
            public int compare(TopicMatch o1, TopicMatch o2) {
                String t1 = o1 != null ? o1.getTopicName() : null;
                String t2 = o2 != null ? o2.getTopicName() : null;
                if (t1 == null) {
                    t1 = "";
                }
                if (t2 == null) {
                    t2 = "";
                }
                t1 = t1.toLowerCase();
                t2 = t2.toLowerCase();

                MatchWeight m1 = o1.getMatchWeight();
                MatchWeight m2 = o2.getMatchWeight();

                String padding1 = getMatchWeightPadding(m1);
                String padding2 = getMatchWeightPadding(m2);

                if (padding1.equals(padding2)) {
                    int ranking1 = getLessonRanking(o1.getTopic().getFile());
                    int ranking2 = getLessonRanking(o2.getTopic().getFile());

                    // then only compare ranking
                    int res = ranking1 - ranking2;
                    return res;
                } else {
                    // different sections
                    t1 = padding1 + t1;
                    t2 = padding2 + t2;
                    return t1.compareTo(t2);
                }
            }
        });
        return ordered;
    }

    final static int HIGHEST_POSSIBLE_RANK = 50000;

    private int getLessonRanking(String file) {
        Integer rank = __rankings.get(file);
        if (rank != null) {
            return rank;
        } else {
            return HIGHEST_POSSIBLE_RANK;
        }
    }

    private String getMatchWeightPadding(MatchWeight m1) {
        int numSpaces = m1.ordinal() + 1;
        String padding = "";
        for (int i = 0; i < numSpaces; i++) {
            padding += " ";
        }
        return padding;
    }

    /**
     * SEE CM-996
     *
     * @param t
     * @param search
     * @return
     */
    private MatchWeight determineMatchWeight(Topic t, String search) {
        if (_checkTitleMatchesAbsolute(t, search)) {
            return MatchWeight.TITLE_MATCH_ABSOLUTE;
        } else if (_checkTitleMatchesAbsolutePlus(t, search)) {
            return MatchWeight.TITLE_MATCH_ALL_PLUS;
        } else if (_checkContentContainsAll(t, search)) {
            return MatchWeight.CONTENT_MATCH_ALL;
        } else if (_checkTitleContainsSome(t, search)) {
            return MatchWeight.TITLE_MATCH_SOME;
        } else if (_checkContentContainsSome(t, search)) {
            return MatchWeight.CONTENT_MATCH_SOME;
        } else if(_checkContentIsPid(t, search)) {
            return MatchWeight.SOLUTION_CONTENT_MATCH;
        } else {
            return MatchWeight.CONTENT_MATCH_SIMPLE;
        }
    }

    private boolean _checkContentContainsSome(Topic topic, String search) {
        String t = topic.getExcerpt();
        if (containsSomeSearchTerms(t, search)) {
            return true;
        }
        return false;
    }
    
    private boolean _checkContentIsPid(Topic topic, String search) {
        ProblemID p = new ProblemID(topic.getExcerpt());
        return p.getChapter() != null;
    }
    

    private boolean _checkTitleContainsSome(Topic topic, String search) {
        String t = topic.getName();

        if (containsSomeSearchTerms(t, search)) {
            return true;
        }
        return false;
    }

    private boolean _checkContentContainsAll(Topic t, String search) {
        String content = t.getExcerpt();

        if (containsAllSearchTerms(content, search)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean _checkTitleMatchesAbsolute(Topic topic, String search) {
        String t = topic.getName();
        if (containsAllSearchTerms(t, search) && t.length() == search.length()) {
            return true;
        }
        return false;
    }

    private boolean _checkTitleMatchesAbsolutePlus(Topic topic, String search) {
        String t = topic.getName();
        if (containsAllSearchTerms(t, search)) {
            return true;
        }
        return false;
    }

    private boolean containsAllSearchTerms(String name, String search) {
        String words[] = search.toLowerCase().split(" ");
        name = name.toLowerCase();
        for (String w : words) {
            if (!name.contains(w)) {
                return false;
            }
        }
        return true;
    }

    private boolean containsSomeSearchTerms(String name, String search) {
        String words[] = search.toLowerCase().split(" ");
        name = name.toLowerCase();
        for (String w : words) {
            if (name.contains(w)) {
                return true;
            }
        }
        return false;
    }

    static public void main(String as[]) {
        try {
            HMIndexWriter hmIw = HMIndexWriterFactory.getHMIndexWriter("inmh");
            hmIw.writeIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
