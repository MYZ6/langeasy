package com.chenyi.langeasy.capture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

public class SentenceFinder {
	public static void main(String[] args) throws IOException, JSONException,
			SQLException {
		Connection conn = CaptureUtil.getConnection();
		listWord(conn);
		CaptureUtil.closeConnection(conn);
	}

	private static List<Map<String, Object>> sentenceLst = new ArrayList<>();

	private static List<Map<String, String>> listWord(Connection conn)
			throws JSONException, SQLException, FileNotFoundException,
			IOException {
		String sql = "SELECT v.id, v.word from vocabulary v left join vocabulary_audio r on r.wordid = v.id "
				+ "where r.wordid is null group by v.id";
		sql = "SELECT v.id, s.word, s.wordid, COUNT(*) FROM langeasy.vocabulary_audio s INNER JOIN vocabulary v ON v.id = s.wordid "
				+ "where 1=1 and v.word = 'covetous' GROUP BY s.word HAVING COUNT(*)<6 ORDER BY 4 DESC, word limit 1";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<Map<String, Object>> wordLst = new ArrayList<>();
		while (rs.next()) {
			Integer wordid = rs.getInt("id");
			String word = rs.getString("word");
			Map<String, Object> wordMap = new HashMap<>();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordLst.add(wordMap);
		}
		rs.close();
		st.close();

		int count = 0;
		for (Map<String, Object> map : wordLst) {
			Integer wordid = (Integer) map.get("wordid");
			String word = (String) map.get("word");
			handleWord(conn, wordid, word);
			System.out.println("find seq : " + count);
			if (count % 50 == 3) {
				insertSentence(conn);
				sentenceLst = new ArrayList<>();
			}
			count++;
		}
		System.out.println(sentenceLst.size());
		insertSentence(conn);

		return null;
	}

	private static void handleWord(Connection conn, Integer wordid, String word)
			throws JSONException, SQLException, FileNotFoundException,
			IOException {
		String sql = "select * from langeasy.sentence s where s.type = 'orig' and s.text != '' and "
				+ "s.text REGEXP concat( '[[:<:]]', '" + word + "', '[[:>:]]')";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			Integer sentenceid = rs.getInt("id");
			String text = rs.getString("text");

			Map<String, Object> wordMap = new HashMap<>();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordMap.put("sentenceid", sentenceid);
			wordMap.put("sentence", text);
			sentenceLst.add(wordMap);

			System.out.println(word);
			System.out.println(sentenceid);
			System.out.println(text);
			// break;
		}
		rs.close();
		st.close();
	}

	private static Integer insertSentence(Connection conn)
			throws JSONException, SQLException {
		String insertSql = "INSERT INTO vocabulary_audio (wordid, word, sentenceid, sentence, ctime) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (Map<String, Object> wordMap : sentenceLst) {
			insPs.setInt(1, (int) wordMap.get("wordid"));
			insPs.setString(2, (String) wordMap.get("word"));
			insPs.setInt(3, (int) wordMap.get("sentenceid"));
			insPs.setString(4, (String) wordMap.get("sentence"));
			insPs.setTimestamp(5, new Timestamp(new Date().getTime()));
			insPs.addBatch();
		}
		try {
			insPs.executeBatch();
		} catch (BatchUpdateException exception) {
			exception.printStackTrace();
			String message = exception.getMessage();
			System.out.println(message);
			if (message.indexOf("vocabularyaudio_unique") > -1) {
				System.out.println("relation is alreay exist.");
			} else {
				throw exception;
			}
		}
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
