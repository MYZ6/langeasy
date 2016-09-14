package com.chenyi.langeasy.sqlite;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chenyi.langeasy.capture.CaptureUtil;

public class SentenceLoader {
	private static Connection conn;

	public static void main(String[] args) {
		try {
			conn = SqliteHelper.getConnection(null);

			listSentence();

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	private static void listSentence() throws SQLException, FileNotFoundException, IOException {
		String sql = "SELECT v.id as wordid, v.word, v.pron, m4.meaning, m4.type AS mtype, a.sentenceid, a.sentence, a.chinese "
				+ "FROM langeasy.vocabulary v LEFT JOIN meaning m4 ON m4.id = v.meaningid "
				+ "LEFT JOIN langeasy.vocabulary_audio a ON a.wordid = v.id WHERE a.id IS NOT NULL";
		Connection mysqlConn = CaptureUtil.getConnection();
		Statement st = mysqlConn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<Map<String, Object>> recordLst = new ArrayList<>();
		while (rs.next()) {
			Integer sentenceid = rs.getInt("sentenceid");
			Integer wordid = rs.getInt("wordid");
			String word = rs.getString("word");
			String pron = rs.getString("pron");
			String mtype = rs.getString("mtype");
			String meaning = rs.getString("meaning");
			String sentence = rs.getString("sentence");
			String chinese = rs.getString("chinese");
			Map<String, Object> map = new HashMap<>();
			map.put("wordid", wordid);
			map.put("sentenceid", sentenceid);
			map.put("word", word);
			map.put("pron", pron);
			map.put("mtype", mtype);
			map.put("meaning", meaning);
			map.put("sentence", sentence);
			map.put("chinese", chinese);
			recordLst.add(map);
		}
		rs.close();
		st.close();
		mysqlConn.close();

		String isql = "INSERT INTO sentence (sentenceid, wordid, word, pron, mtype, meaning, sentence, chinese) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		PreparedStatement insPs = conn.prepareStatement(isql);
		int count = 0;
		for (Map<String, Object> map : recordLst) {
			count++;
			System.out.println("find seq : " + count);

			Integer sentenceid = (Integer) map.get("sentenceid");
			Integer wordid = (Integer) map.get("wordid");
			String word = (String) map.get("word");
			String pron = (String) map.get("pron");
			String mtype = (String) map.get("mtype");
			String meaning = (String) map.get("meaning");
			String sentence = (String) map.get("sentence");
			String chinese = (String) map.get("chinese");

			// set parameters
			insPs.setInt(1, sentenceid);
			insPs.setInt(2, wordid);
			insPs.setString(3, word);
			insPs.setString(4, pron);
			insPs.setString(5, mtype);
			insPs.setString(6, meaning);
			insPs.setString(7, sentence);
			insPs.setString(8, chinese);
			insPs.addBatch();

			int step = 266;
			if (count % step == 96) {
				System.out.println("\texecute times : " + count / step);
				try {
					insPs.executeBatch();
				} catch (BatchUpdateException exception) {
					exception.printStackTrace();
					String message = exception.getMessage();
					System.out.println(message);
					if (message.indexOf("primary") > -1) {
						System.out.println(wordid + " is alreay exist.");
					} else {
						throw exception;
					}
				}
			}
		}
		try {
			insPs.executeBatch();
		} catch (BatchUpdateException exception) {
			exception.printStackTrace();
			String message = exception.getMessage();
			System.out.println(message);
			if (message.indexOf("primary") > -1) {
				System.out.println("word is alreay exist.");
			} else {
				throw exception;
			}
		}
		insPs.clearBatch();
		insPs.close();

	}
}
