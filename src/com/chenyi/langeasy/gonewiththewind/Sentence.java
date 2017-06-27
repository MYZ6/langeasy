package com.chenyi.langeasy.gonewiththewind;

import com.chenyi.langeasy.capture.CaptureUtil;
import com.chenyi.langeasy.sqlite.SqliteHelper;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.BreakIterator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Sentence {

	public static void main(String[] args) throws IOException, SQLException {
		Connection conn = SqliteHelper.getConnection("goneWithTheWind");
		loadData(conn);
	}

	public static int loadData(Connection conn) throws SQLException, IOException {
		String dirPath = "E:/langeasy/test-doc/";
		File sFile = new File(dirPath + "0200161.txt");
//		File sFile = new File(dirPath + "novel-test.txt");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");


		String isql = "INSERT INTO sentence (sentence) VALUES (?)";
		conn.setAutoCommit(false);
		PreparedStatement insPs = conn.prepareStatement(isql);

		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(sResult);
		int start = iterator.first();
		for (int end = iterator.next();
			 end != BreakIterator.DONE;
			 start = end, end = iterator.next()) {
			String sentence = sResult.substring(start, end);
			sentence = sentence.replaceAll("\r\n", " ");
			sentence = sentence.trim();

			insPs.setString(1, sentence);
			insPs.addBatch();
		}
		try {
			insPs.executeBatch();
			conn.commit();
		} catch (BatchUpdateException exception) {
			exception.printStackTrace();
		}
		insPs.clearBatch();
		insPs.close();

		return 0;
	}


	static JSONArray list(Connection conn, String word)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT * from sentence where sentence like '%" + word + "%'";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer sentenceId = rs.getInt("id");
			String sentence = rs.getString("sentence");
			JSONObject sentenceMap = new JSONObject();
			sentenceMap.put("sentenceId", sentenceId);
			sentenceMap.put("sentence", sentence);
			arr.put(sentenceMap);
		}
		rs.close();
		st.close();

		return arr;
	}
}
