package com.chenyi.langeasy.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Vocabulary {

	static JSONArray listWord(Connection conn) throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT id, word, pron from vocabulary limit 30";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("id");
			String word = rs.getString("word");
			String pron = rs.getString("pron");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordMap.put("pron", pron);
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	static JSONArray listAword(Connection conn) throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT wordid, word from vocabulary_audio group by wordid limit 2";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			JSONObject wordMap = new JSONObject();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			arr.put(wordMap);
		}
		rs.close();
		st.close();

		return arr;
	}

	public static JSONObject word(Connection conn, Integer wordid) throws SQLException {
		String sql = "SELECT word, pron, mp3path, oggpath from vocabulary where id = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, wordid);
		ResultSet rs = st.executeQuery();
		JSONObject wordMap = new JSONObject();
		String pronPrefix = "https://www.oxforddictionaries.com/media/english/";
		while (rs.next()) {
			String word = rs.getString("word");
			String pron = rs.getString("pron");
			String mp3path = rs.getString("mp3path");
			String oggpath = rs.getString("oggpath");
			oggpath = oggpath.substring(pronPrefix.length());
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordMap.put("pron", pron);
			wordMap.put("mp3path", mp3path);
			wordMap.put("oggpath", oggpath);
		}
		rs.close();
		st.close();

		wordMap.put("meaning", listMeaning(conn, wordid));
		wordMap.put("aexample", listAudioExample(conn, wordid));
		return wordMap;
	}

	public static JSONArray listMeaning(Connection conn, Integer wordid) throws SQLException {
		String sql = "SELECT id, type, meaning from meaning where wordid = ? order by weight";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, wordid);
		ResultSet rs = st.executeQuery();
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer meaningid = rs.getInt("id");
			String type = rs.getString("type");
			String meaning = rs.getString("meaning");

			JSONObject map = new JSONObject();
			map.put("meaningid", meaningid);
			map.put("type", type);
			map.put("meaning", meaning);
			arr.put(map);
		}
		rs.close();
		st.close();

		for (int i = 0; i < arr.length(); i++) {
			JSONObject item = arr.getJSONObject(i);
			item.put("example", listExample(conn, item.getInt("meaningid")));
		}

		return arr;
	}

	public static JSONArray listExample(Connection conn, Integer meaningid) throws SQLException {
		String sql = "SELECT id, sentence from example_sentence where meaningid = ? order by weight";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, meaningid);
		ResultSet rs = st.executeQuery();
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer exampleid = rs.getInt("id");
			String sentence = rs.getString("sentence");

			JSONObject map = new JSONObject();
			map.put("exampleid", exampleid);
			map.put("sentence", sentence);
			arr.put(map);
		}
		rs.close();
		st.close();

		return arr;
	}

	public static JSONArray listAudioExample(Connection conn, Integer wordid) throws SQLException {
		String sql = "SELECT sentenceid, sentence from vocabulary_audio where wordid = ?";
		PreparedStatement st = conn.prepareStatement(sql);
		st.setInt(1, wordid);
		ResultSet rs = st.executeQuery();
		JSONArray arr = new JSONArray();
		while (rs.next()) {
			Integer sentenceid = rs.getInt("sentenceid");
			String sentence = rs.getString("sentence");

			JSONObject map = new JSONObject();
			map.put("sentenceid", sentenceid);
			map.put("sentence", sentence);
			arr.put(map);
		}
		rs.close();
		st.close();

		return arr;
	}
}
