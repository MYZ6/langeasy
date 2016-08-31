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

public class EncryptTimeHandler {

	public static void main(String[] args) throws IOException, JSONException, SQLException {
		Connection conn = CaptureUtil.getConnection();
		listSentence(conn);
		updateDecodetime(conn);
	}

	private static List<Map<String, Object>> sentenceLst = new ArrayList<>();

	private static List<Map<String, String>> listSentence(Connection conn)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "select id, encryptstarttime from sentence where decodestarttime is null limit 20000";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			Integer sentenceid = rs.getInt("id");
			String encryptstarttime = rs.getString("encryptstarttime");
			Map<String, Object> map = new HashMap<>();
			map.put("sentenceid", sentenceid);
			map.put("encryptstarttime", encryptstarttime);
			sentenceLst.add(map);
		}
		rs.close();
		st.close();

		return null;
	}

	private static Integer updateDecodetime(Connection conn) throws JSONException, SQLException {
		String usql = "update sentence set decodestarttime = ? where id = ?";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		int count = 0;
		for (Map<String, Object> map : sentenceLst) {
			Integer sentenceid = (Integer) map.get("sentenceid");
			String encryptstarttime = (String) map.get("encryptstarttime");
			int decodestarttime = CaptureUtil.decodeTime(encryptstarttime);

			updatePs.setInt(1, decodestarttime);
			updatePs.setInt(2, sentenceid);
			updatePs.addBatch();

			if (count % 5 == 3) {
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
