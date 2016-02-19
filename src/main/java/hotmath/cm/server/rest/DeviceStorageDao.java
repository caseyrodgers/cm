package hotmath.cm.server.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import hotmath.cm.server.model.DeviceStorage;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

public class DeviceStorageDao {
	
	
	static void saveStorage(String deviceId, DeviceStorage storage) throws Exception {
		Connection conn=null;
		PreparedStatement ps=null;
		PreparedStatement psD=null;
		try {
			conn = HMConnectionPool.getConnection();
			
			String sqlD = "delete from CM_DEVICE_STORAGE where device_id = ? and key_name = ?";
			psD = conn.prepareStatement(sqlD);
			psD.setString(1,  deviceId);

			String sql = "insert into CM_DEVICE_STORAGE(device_id, key_name, key_data)values(?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1,  deviceId);
			for(String keyName: storage.getStorage().keySet()) {
				
				if(keyName.startsWith("@")) {
					continue;
				}

				String keyData = storage.getStorage().get(keyName);
				
				/** remove that key if exists */
				psD.setString(2,  keyName);;
				psD.executeUpdate();
				
				/** add key to db */
				ps.setString(2,  keyName);
				ps.setString(3,  keyData);
				if(ps.executeUpdate() != 1) {
					System.out.println("warning could not save device storage data for device: " + deviceId);
				}
			}
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
	
	static DeviceStorage getStorage(String deviceId) throws Exception {
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn = HMConnectionPool.getConnection();

		    DeviceStorage storage = new DeviceStorage();
			String sql = "select * from CM_DEVICE_STORAGE where device_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,  deviceId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String key = rs.getString("key_name");
				String keyData = rs.getString("key_data");
				storage.getStorage().put(key,  keyData);
			}
		    return storage.getStorage().size() > 0?storage:null;
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
	
	
	static void deleteStorage(String deviceId) throws Exception {
		Connection conn=null;
		PreparedStatement ps=null;
		try {
			conn = HMConnectionPool.getConnection();

			String sql = "delete from CM_DEVICE_STORAGE where device_id = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,  deviceId);
			ps.executeUpdate();
		}
		finally {
			SqlUtilities.releaseResources(null, null, conn);
		}
	}
	

}
