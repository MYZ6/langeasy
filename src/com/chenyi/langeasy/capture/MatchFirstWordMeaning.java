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

public class MatchFirstWordMeaning {

	public static void main(String[] args) throws IOException, JSONException, SQLException {
		Connection conn = CaptureUtil.getConnection();
		listSentence(conn);
		updateMeaningId(conn);
		conn.close();
	}

	private static List<Map<String, Object>> sentenceLst = new ArrayList<>();

	private static List<Map<String, String>> listSentence(Connection conn)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT m2.id, m2.wordid FROM langeasy.meaning m2 INNER JOIN ( SELECT m.wordid, MIN(m.weight) AS weight FROM langeasy.meaning m GROUP BY m.wordid) m3 ON m3.wordid = m2.wordid AND m3.weight = m2.weight";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			Integer meaningid = rs.getInt("id");
			Integer wordid = rs.getInt("wordid");
			Map<String, Object> map = new HashMap<>();
			map.put("meaningid", meaningid);
			map.put("wordid", wordid);
			sentenceLst.add(map);
		}
		rs.close();
		st.close();

		return null;
	}

	private static Integer updateMeaningId(Connection conn) throws JSONException, SQLException {
		String usql = "update vocabulary set meaningid = ? where id = ?";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		int count = 0;
		for (Map<String, Object> map : sentenceLst) {
			Integer meaningid = (Integer) map.get("meaningid");
			Integer wordid = (Integer) map.get("wordid");

			updatePs.setInt(1, meaningid);
			updatePs.setInt(2, wordid);
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
