package com.chenyi.langeasy.capture.pron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chenyi.langeasy.capture.CaptureUtil;

public class TransformPronFilePath {

	public static void main(String[] args) throws SQLException, JSONException, FileNotFoundException, IOException {
		Connection conn = CaptureUtil.getConnection();
		listAword(conn);
		conn.close();
	}

	static JSONArray listAword(Connection conn) throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT a.wordid, a.word, v.oggpath from vocabulary_audio a inner join vocabulary v on v.id = a.wordid group by a.wordid limit 20000";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		String pronPrefix = "https://www.oxforddictionaries.com/media/english/";
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			String oggpath = rs.getString("oggpath");
			oggpath = oggpath.substring(pronPrefix.length());
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordMap.put("oggpath", oggpath);
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		for (int i = 0; i < arr.length(); i++) {
			JSONObject json = arr.getJSONObject(i);
			Integer wordid = json.getInt("wordid");
			String oggpath = json.getString("oggpath");
			String filepath = "e:/langeasy/pronunciation/" + oggpath;
			String newpath = "e:/langeasy/pronunciation-ogg/" + wordid + ".ogg";
			FileUtils.copyFile(new File(filepath), new File(newpath));
		}

		return arr;
	}
}
