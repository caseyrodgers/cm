package hotmath.gwt.cm_admin.client.ui.highlights;

import com.extjs.gxt.ui.client.data.BaseModelData;

/** Class to encapsulate table data for report output
 * 
 *  NOTE: Really two models... one for individual and one for groups.
 *  
 * @author casey
 *
 */
public class HighlightsReportModel extends BaseModelData {
    int uid;
    int quizzesViewed;
    
    public HighlightsReportModel() {}
    
    public HighlightsReportModel(Integer uid, String name, String data) {
        this.uid = uid;
        set("name", name);
        
        /** allow up to four data variables to be
         * passed separated by |.  Each value 
         * will be in data1, data2, etc..
         * 
         */
        if(data != null) {
            String ds[] = data.split("\\|");
            for(int i=0;i<ds.length;i++) {
                set("data" + (i+1), ds[i]);    
            }
        }
    }
    
    public HighlightsReportModel(Integer uid, String name, String data, int quizzesTaken) {
        this.uid = uid;
        set("quizzesTaken", quizzesTaken);
        set("name", name);
        
        /** allow up to four data variables to be
         * passed separated by |.  Each value 
         * will be in data1, data2, etc..
         * 
         */
        if(data != null) {
            String ds[] = data.split("\\|");
            for(int i=0;i<ds.length;i++) {
                set("data" + (i+1), ds[i]);    
            }
        }
    }    
    
    public HighlightsReportModel(String name, int groupCnt, int schoolCount, int nationalCount) {
        set("name", name);
        set("group", groupCnt);
        set("school", schoolCount);
        set("national", nationalCount);
    }

    
    /** for group compare */
    public HighlightsReportModel(String name, int activeCount, int videosViewed, int gamesViewed, int activitiesViewed, int flashCardsViewed) { 
        set("name", name);
        set("activeCount", activeCount);
        set("videosViewed", videosViewed);
        set("gamesViewed", gamesViewed);
        set("activitiesViewed", activitiesViewed);
        set("flashcardsViewed", flashCardsViewed);
    }

    public String getName() {
        return get("name");
    }

    public String getData1() {
        return get("data1");
    }
    
    public String getData2() {
        return get("data2");
    }
    
    public String getData3() {
        return get("data3");
    }
    
    public String getData4() {
        return get("data4");
    }
    
    public Integer getUid() {
        return uid;
    }
    
    
    public String[] getReportLabels() {
        String labels[] = {"Name", "Lessons Viewed"};
        return labels;
    }
}