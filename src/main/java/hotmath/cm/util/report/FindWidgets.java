package hotmath.cm.util.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

/** Find random problems with each distinct widget type 
 *  
 * @author casey
 *
 */
public class FindWidgets {

	public void _findWidgets() throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		
		
	    Map<String,String> set = new HashMap<String,String>();

		try {
			conn = HMConnectionPool.getConnection();

			String sqlPid = "select solutionxml from SOLUTIONS where problemindex = ?";
			ps = conn.prepareStatement(sqlPid);
			String sql = "select problemindex from SOLUTIONS where solutionxml like '%hm_flash_widget%' order by rand() ";

			ResultSet rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				String pid = rs.getString("problemindex");

				ps.setString(1, pid);
				;

				ResultSet rs2 = ps.executeQuery();
				if (rs2.next()) {
					String widgetType = getWidgetType(rs2.getString("solutionxml"));
					if(!set.containsKey(widgetType)) {
						set.put(widgetType,  pid);
					}
				}
			}
			
			for(String type: set.keySet()) {
				System.out.println(type + ": " + set.get(type));
			}

		} finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}

	private String getWidgetType(String xml) {
		String type = "UNKNOWN";
		String searchFor = "{type:'";
		String searchEnd = "',";
		int start = xml.indexOf(searchFor);
		if (start > -1) {
			int end = xml.indexOf(searchEnd, start + 1);
			type = xml.substring(start + (searchFor.length()), end);
		}

		return type;
	}

	static public void main(String as[]) {
		try {
			new FindWidgets()._findWidgets();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
