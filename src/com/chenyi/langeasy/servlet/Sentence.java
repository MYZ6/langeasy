package com.chenyi.langeasy.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chenyi.langeasy.sqlite.SqliteHelper;

public class Sentence {
	public static JSONArray listSentenceByDB() {
		// File bookFile = new File("E:\\dB.json");
		File bookFile = new File("E:\\dB2.json");
		JSONArray arr = null;
		try {
			String sResult = IOUtils.toString(new FileInputStream(bookFile), "utf-8");
			arr = new JSONArray(sResult);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<JSONObject> jsonLst = new ArrayList<>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject sentence = arr.getJSONObject(i);
			jsonLst.add(sentence);
		}
		Collections.sort(jsonLst, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject sentence2, JSONObject sentence1) {
				// String mean_volume1 = sentence1.getString("mean_volume2");
				// String mean_volume2 = sentence2.getString("mean_volume2");
				// return Integer.parseInt(mean_volume1) - Integer.parseInt(mean_volume2);
				System.out.println(sentence1.getString("sentence"));
				System.out.println(sentence2.getString("sentence"));
				Double mean_volume1 = sentence1.getDouble("mean_volume2");
				Double mean_volume2 = sentence2.getDouble("mean_volume2");
				return mean_volume1.compareTo(mean_volume2);
			}
		});

		return new JSONArray(jsonLst);
	}

	public static JSONArray listModifiedSentence() throws SQLException {
		Connection conn = SqliteHelper.getConnection("sentence-audio");
		String sql = "SELECT s.sentenceid, s.mtime, DATETIME(s.mtime/1000,'unixepoch', 'localtime') as mtimestr "
				+ "FROM sentence_audio s WHERE mtime is not null ORDER BY 2 DESC limit 10";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<JSONObject> jsonLst = new ArrayList<>();
		while (rs.next()) {
			Integer sentenceid = rs.getInt("sentenceid");
			long mtime = rs.getLong("mtime");
			String mtimestr = rs.getString("mtimestr");
			JSONObject map = new JSONObject();
			map.put("sentenceid", sentenceid);
			map.put("mtime", mtime);
			map.put("mtimestr", mtimestr);
			jsonLst.add(map);
		}
		rs.close();
		st.close();
		conn.close();

		System.out.println(jsonLst);
		return new JSONArray(jsonLst);
	}

	public static void main(String[] args) throws SQLException {
		listModifiedSentence();
	}
}
