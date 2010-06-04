package hotmath.cm.util;

import java.util.List;

/*
 * @author bob
 */

public class QueryHelper {
	
	/**
	 * @param sqlTemplate
	 * @param vals
	 * @param name  the column name
	 * @param limit number of vals per list chunk
	 * @return SQL with vals in one or more comma separated lists
	 * @throws Exception
	 */
	static public String createInListSQL(String sqlTemplate, List<Integer> vals, String name, Integer limit) throws Exception {
		
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		
		int count = vals.size(); 
		int chunks = count / limit;
		
		if ((chunks * limit) < count) chunks += 1;
		
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
		sb.append(" )");
		
		String sql = String.format(sqlTemplate, sb.toString());
		
		return sql;
	}

}