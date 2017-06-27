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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class Vocabulary {

	static JSONArray listWord(Connection conn, String wtype, Integer limit)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String condition = "and ifnull(v.pass, 0) != 1 and ifnull(v.unknown, 0) != 1";
		if ("2".equals(wtype)) {
			condition = "and ifnull(v.pass, 0) = 1";
		} else if ("3".equals(wtype)) {
			condition = "and ifnull(v.unknown, 0) = 1";
		}
		Integer offset = 0;
		if (!"2".equals(wtype)) {
			// offset = queryTotal(conn, "2") + 500;
		}
		if (limit == null) {
			limit = 200;
		}
		String sql = "SELECT wordid, word, pass from vocabulary v where 1=1 " + condition + " "
				+ "order by word limit " + limit + " offset " + offset + "";
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
		String condition = "and ifnull(v.pass, 0) != 1 and ifnull(v.unknown, 0) != 1";
		if ("2".equals(wtype)) {
			condition = "and ifnull(v.pass, 0) = 1";
		} else if ("3".equals(wtype)) {
			condition = "and ifnull(v.unknown, 0) = 1";
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

	public static int unknown(Connection conn, String wordids) throws SQLException {
		String usql = "update vocabulary set unknown = 1, mtime = ? where wordid in (" + wordids + ")";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		updatePs.setTimestamp(1, new Timestamp(new Date().getTime()));
		updatePs.addBatch();
		updatePs.executeBatch();
		updatePs.clearBatch();
		updatePs.close();

		return 0;
	}

	public static String translate(String word) {
		String url = "http://www.iciba.com/" + word;
		Document doc = CaptureUtil.timeoutRequest(url, 100, 5);
		if (doc == null) {
			return "not found";
		}
		Elements content = doc.select(".in-base");// .get(0);
		if (content.size() == 0) {
			return "not found";
		}
		content.select("script").remove();
		content.select(".base-bt-bar").remove();
		content.select(".cb-downloadbar").remove();
		content.select(".scb-con").remove();

		return content.toString();
	}

}
