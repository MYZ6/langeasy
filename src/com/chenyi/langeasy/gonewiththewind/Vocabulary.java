package com.chenyi.langeasy.gonewiththewind;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Vocabulary {

	static JSONArray listWord(Connection conn, String wtype)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String condition = "and ifnull(v.pass, 0) != 1";
		if ("2".equals(wtype)) {
			condition = "and ifnull(v.pass, 0) = 1";
		}
		String sql = "select * from (SELECT wordid, word, pass from vocabulary v where 1=1 " + condition + " "
				+ "order by word limit 200 offset 650) order by word desc";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			Integer pass = rs.getInt("pass");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordMap.put("pass", pass);
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	static int queryTotal(Connection conn, String wtype)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String condition = "and ifnull(v.pass, 0) != 1";
		if ("2".equals(wtype)) {
			condition = "and ifnull(v.pass, 0) = 1";
		}
		String sql = "select count(*) as total from vocabulary v where 1=1 " + condition;
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		Integer total = 0;
		while (rs.next()) {
			total = rs.getInt("total");
		}
		rs.close();
		st.close();

		return total;
	}

	public static int pass(Connection conn, String wordids) throws SQLException {
		String usql = "update vocabulary set pass = 1, mtime = ? where wordid in (" + wordids + ")";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		updatePs.setTimestamp(1, new Timestamp(new Date().getTime()));
		updatePs.addBatch();
		updatePs.executeBatch();
		updatePs.clearBatch();
		updatePs.close();

		return 0;
	}

}
