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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chenyi.langeasy.capture.CaptureUtil;

public class SentenceLoader {
	private static Connection conn;

	public static void main(String[] args) {
		System.out.println("start time is : " + new Date());
		try {
			conn = SqliteHelper.getConnection("data.v2");

			listSentence();

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	private static void listSentence() throws SQLException, FileNotFoundException, IOException {
		String sql = "SELECT v.id as wordid, v.word, v.pron, m4.meaning, m4.type AS mtype, a.sentenceid, a.sentence, a.chinese, "
				+ "b.bookid, b.bookname, b.booktype, c.courseid, c.name AS coursename "
				+ "FROM langeasy.vocabulary v LEFT JOIN meaning m4 ON m4.id = v.meaningid "
				+ "INNER JOIN langeasy.vocabulary_audio a ON a.wordid = v.id "
				+ "INNER JOIN langeasy.sentence s ON s.id = a.sentenceid "
				+ "INNER JOIN langeasy.course c ON c.courseid = s.courseid "
				+ "INNER JOIN langeasy.book b ON b.bookid = c.bookid " + "WHERE 1=1 limit 50000";
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
			String bookid = rs.getString("bookid");
			String bookname = rs.getString("bookname");
			String booktype = rs.getString("booktype");
			String courseid = rs.getString("courseid");
			String coursename = rs.getString("coursename");
			Map<String, Object> map = new HashMap<>();
			map.put("wordid", wordid);
			map.put("sentenceid", sentenceid);
			map.put("word", word);
			map.put("pron", pron);
			map.put("mtype", mtype);
			map.put("meaning", meaning);
			map.put("sentence", sentence);
			map.put("chinese", chinese);
			map.put("bookid", bookid);
			map.put("bookname", bookname);
			map.put("booktype", booktype);
			map.put("courseid", courseid);
			map.put("coursename", coursename);
			recordLst.add(map);
		}
		rs.close();
		st.close();
		mysqlConn.close();

		int total = recordLst.size();
		if (total > 0) {
			System.out.println("total: " + total);
			// return;
		}

		String isql = "INSERT INTO sentence (sentenceid, wordid, word, pron, mtype, meaning, sentence, chinese, bookid, bookname, booktype, courseid, coursename) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		conn.setAutoCommit(false);
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
			String bookid = (String) map.get("bookid");
			String bookname = (String) map.get("bookname");
			String booktype = (String) map.get("booktype");
			String courseid = (String) map.get("courseid");
			String coursename = (String) map.get("coursename");

			// set parameters
			insPs.setInt(1, sentenceid);
			insPs.setInt(2, wordid);
			insPs.setString(3, word);
			insPs.setString(4, pron);
			insPs.setString(5, mtype);
			insPs.setString(6, meaning);
			insPs.setString(7, sentence);
			insPs.setString(8, chinese);
			insPs.setString(9, bookid);
			insPs.setString(10, bookname);
			insPs.setString(11, booktype);
			insPs.setString(12, courseid);
			insPs.setString(13, coursename);
			insPs.addBatch();

			int step = 1566;
			if (count % step == 1536) {
				System.out.println("\texecute times : " + count / step);
				try {
					insPs.executeBatch();
					conn.commit();
					System.out.println("batch end time is : " + new Date());
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
			conn.commit();
			System.out.println("batch end time is : " + new Date());
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
