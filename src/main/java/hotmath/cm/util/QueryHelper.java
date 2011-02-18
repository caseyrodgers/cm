package hotmath.cm.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/*
 * @author bob
 */

public class QueryHelper {
	
	/**
	 * @param sqlTemplate A string containing token $$UID_LIST$$ which will be replaced. (using named token to help document SQL)
	 * @param vals
	 * @param name  the column name
	 * @return SQL with vals in one or more comma separated lists
	 * @throws Exception
	 */
    static public String createInListSQL(String sqlTemplate, List<Integer> vals, String name) throws Exception {
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        
        int count = vals.size(); 
        if(count == 0) {
            /** handle empty list with valid SQL 
             * 
             */
            sb.append(name).append(" in (NULL)");
        }
        else {
            sb.append(name).append(" IN (");
            int cnt=0;
            for(Integer v: vals) {
                if(++cnt > 1) 
                    sb.append(", ");
                sb.append(v);
            }
            sb.append(")");
        }
        sb.append(" )");
        
        String sql = sqlTemplate.replace("$$UID_LIST$$", sb.toString());
        
        return sql;
    }

	
	/**
	 * @param sqlTemplate A string containing token $$UID_LIST$$ which will be replaced. (using named token to help document SQL)
	 * @param vals
	 * @param name  the column name
	 * @param limit number of vals per list chunk
	 * @return SQL with vals in one or more comma separated lists
	 * @throws Exception
	 */
    static public String createInListMultiSQL(String sqlTemplate, List<Integer> vals, String name, int limit) throws Exception {
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        
        int count = vals.size(); 
        int chunks = count / limit;
        
        if ((chunks * limit) < count) chunks += 1;
        
        if(count == 0) {
            /** handle empty list with valid SQL 
             * 
             */
            sb.append(name).append(" in (NULL)");
        }
        else {
            int j = 0;
            for (int i=0; i<chunks; i++) {
    
                int k = j + limit;
                if (k > count) k = count;
    
                boolean first = true;
                for (; j < k; j++) {
                    
                    if (first) {
                        first = false;
                        sb.append(name).append(" IN (");
                    }
                    else {
                        sb.append(", ");
                    }
                    sb.append(vals.get(j));
                }
                sb.append(")");
                
                if (k < count) {
                    sb.append(" OR " );
                }
            }
        }
        sb.append(" )");
        
        String sql = sqlTemplate.replace("$$UID_LIST$$", sb.toString());
        
        return sql;
    }
    
    
	/**
	 * @param sqlTemplate A string containing token $$UID_LIST$$ which will be replaced. (using named token to help document SQL)
	 * @param vals
	 * @param name  the column name
	 * @return SQL with vals in concatenated name=val[0] OR name=val[1]... conditions
	 * @throws Exception
	 */
    static public String createOrListSQL(String sqlTemplate, List<Integer> vals, String name) throws Exception {
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        
        int count = vals.size(); 
        
        if(count == 0) {
            /** handle empty list with valid SQL 
             * 
             */
            sb.append(name).append(" is NULL ");
        }
        else {
            for(Integer v: vals) {
                if(sb.length()>1)
                    sb.append(" OR ");
                
                sb.append(name).append(" = ").append(v);
            }
        }
        sb.append(")");
        
        String sql = sqlTemplate.replace("$$UID_LIST$$", sb.toString());
        
        return sql;
    }    

    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    
    static public String getDateTime(Date date, boolean begin) {
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append(fmt.format(date));
    	if (begin) {
    		sb.append(" 00:00:00");
    	}
    	else {
    		sb.append(" 23:59:59");    		
    	}
    	return sb.toString();
    }
    
    static public String[] getDateTimeRange(Date beginDate, Date endDate) {
    	String[] vals = new String[2];
    	
    	vals[0] = getDateTime(beginDate, true);
    	
    	vals[1] = getDateTime(endDate, false);
    	
    	return vals;
    }
}