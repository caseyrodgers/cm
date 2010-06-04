package hotmath.cm.util;

import java.util.List;

import sb.util.SbUtilities;

/*
 * @author bob
 */

public class QueryHelper {
	
	/**
	 * @param sqlTemplate A string containing token $$UID_LIST$$ which will be replaced. (using named token to help document SQL)
	 * @param vals
	 * @param name  the column name
	 * @param limit number of vals per list chunk
	 * @return SQL with vals in one or more comma separated lists
	 * @throws Exception
	 */
    static public String createInListSQL(String sqlTemplate, List<Integer> vals, String name) throws Exception {
        return createInListSQL(sqlTemplate, vals, name, 10);
    }
    
	static public String createInListSQL(String sqlTemplate, List<Integer> vals, String name, int limit) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		
		int count = vals.size(); 
		
		if(count == 0) {
		    /** handle empty list with valid SQL 
		     * 
		     */
		    sb.append(name + " is NULL ");
		}
		else {
    		for(Integer v: vals) {
    		    if(sb.length()>1)
    		        sb.append(" OR ");
    		    
    		    sb.append(name + " = " + v);
    		}
		}
		sb.append(")");
		
		String sql = sqlTemplate.replace("$$UID_LIST$$", sb.toString());
		
		return sql;
	}
	
    static public String createInListSQL2(String sqlTemplate, List<Integer> vals, String name, int limit) throws Exception {
        
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        
        int count = vals.size(); 
        int chunks = count / limit;
        
        if ((chunks * limit) < count) chunks += 1;
        
        if(count == 0) {
            /** handle empty list with valid SQL 
             * 
             */
            sb.append(name + " in (NULL)");
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

}