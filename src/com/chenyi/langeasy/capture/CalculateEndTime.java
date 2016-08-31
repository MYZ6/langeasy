package com.chenyi.langeasy.capture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

public class CalculateEndTime {

	public static void main(String[] args) throws IOException, JSONException, SQLException {
		Connection conn = CaptureUtil.getConnection();
		listSentence(conn);
		updateEndTime(conn);
	}

	private static List<Map<String, Object>> sentenceLst = new ArrayList<>();

	private static List<Map<String, String>> listSentence(Connection conn)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT s.id, s2.decodestarttime "
				+ "FROM sentence s LEFT JOIN sentence s2 ON s.courseid = s2.courseid AND s2.dataindex = s.dataindex + 1 AND s.type = s2.type "
				+ "WHERE s.type='orig' and s.endtime is null limit 10000";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			Integer sentenceid = rs.getInt("id");
			int endtime = rs.getInt("decodestarttime");
			Map<String, Object> map = new HashMap<>();
			map.put("sentenceid", sentenceid);
			map.put("endtime", endtime);
			sentenceLst.add(map);
		}
		rs.close();
		st.close();

		return null;
	}

	private static Integer updateEndTime(Connection conn) throws JSONException, SQLException {
		String usql = "update sentence set endtime = ? where id = ?";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		int count = 0;
		for (Map<String, Object> map : sentenceLst) {
			Integer sentenceid = (Integer) map.get("sentenceid");
			int endtime = (int) map.get("endtime");

			updatePs.setInt(1, endtime);
			updatePs.setInt(2, sentenceid);
			updatePs.addBatch();

			if (count % 500 == 3) {
				updatePs.executeBatch();
			}
			System.out.println("handle seq : " + count);
			count++;
		}
		updatePs.clearBatch();
		updatePs.close();

		return 0;
	}
}
