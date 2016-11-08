package com.chenyi.langeasy.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
		String sql = "SELECT a.wordid, a.word, v.pass from vocabulary_audio a inner join vocabulary v on v.id = a.wordid group by a.wordid limit 20000";
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

	public static int pass(Connection conn, int wordid) throws SQLException {
		String usql = "update vocabulary set pass = 1, mtime = ? where id = ?";
		PreparedStatement updatePs = conn.prepareStatement(usql);

		updatePs.setTimestamp(1, new Timestamp(new Date().getTime()));
		updatePs.setInt(2, wordid);
		updatePs.addBatch();
		updatePs.executeBatch();
		updatePs.clearBatch();
		updatePs.close();

		return 0;
	}

	public static String translate(String word) throws SQLException, ClientProtocolException, IOException {
		String url = "http://fanyi.baidu.com/transapi?from=en&type=1&domain=all$source=txt&to=zh&query=" + word;
		System.out.println(url);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);

		CloseableHttpResponse response = httpclient.execute(httppost);
		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		byte[] result = null;
		// If the response does not enclose an entity, there is no need
		// to bother about connection release
		if (entity != null) {
			InputStream instream = entity.getContent();
			result = IOUtils.toByteArray(instream);
			instream.close();
		}
		response.close();

		String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		JSONObject json = null;
		try {
			json = new JSONObject(sResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(json);
		JSONArray data = json.getJSONArray("data");
		JSONObject data1 = data.getJSONObject(0);
		String dst = data1.getString("dst");
		System.out.println(dst);
		System.out.println(data);

		return dst;
	}
}
