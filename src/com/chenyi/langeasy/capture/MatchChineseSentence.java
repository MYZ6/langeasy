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

public class MatchChineseSentence {

	public static void main(String[] args) throws IOException, JSONException,
			SQLException {
		Connection conn = CaptureUtil.getConnection();
		listSentence(conn);
		updateChinese(conn);
		conn.close();
	}

	private static List<Map<String, Object>> sentenceLst = new ArrayList<>();

	private static List<Map<String, String>> listSentence(Connection conn)
			throws JSONException, SQLException, FileNotFoundException,
			IOException {
		String sql = "SELECT a.id, s2.text AS chinese FROM langeasy.vocabulary_audio a "
				+ "INNER JOIN langeasy.sentence s ON s.id = a.sentenceid INNER JOIN langeasy.sentence s2 ON s2.dataindex = s.dataindex AND s2.courseid = s.courseid AND s2.type = 'i18n' "
				+ "where a.chinese is null limit 10000";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			Integer recordid = rs.getInt("id");
			String chinese = rs.getString("chinese");
			Map<String, Object> map = new HashMap<>();
			map.put("recordid", recordid);
			map.put("chinese", chinese);
			sentenceLst.add(map);
		}
		rs.close();
		st.close();

		return null;
	}

	private static Integer updateChinese(Connection conn) throws JSONException,
			SQLException {
		String usql = "update vocabulary_audio set chinese = ? where id = ?";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		int count = 0;
		for (Map<String, Object> map : sentenceLst) {
			Integer recordid = (Integer) map.get("recordid");
			String chinese = (String) map.get("chinese");

			updatePs.setString(1, chinese);
			updatePs.setInt(2, recordid);
			updatePs.addBatch();

			if (count % 500 == 3) {
				updatePs.executeBatch();
			}
			System.out.println("handle seq : " + count);
			count++;
		}
		updatePs.executeBatch();
		updatePs.clearBatch();
		updatePs.close();

		return 0;
	}
}
