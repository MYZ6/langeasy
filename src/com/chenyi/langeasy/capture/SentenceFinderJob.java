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

public class SentenceFinderJob {

	public static void main(String[] args) throws Exception {
		SentenceFinderJob jobLst = new SentenceFinderJob();
		System.out.println("start time is : " + new Date());
		// return;
		for (int i = 0; i < 7; i++) {
			Job job = jobLst.new Job(i);
			job.start();
		}
	}

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			Connection conn = CaptureUtil.getConnection();

			try {
				listWord(conn, jobIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}

			CaptureUtil.closeConnection(conn);
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}

	private static void listWord(Connection conn, int jobIndex)
			throws JSONException, SQLException, FileNotFoundException,
			IOException {
		int step = 200;
		int startSeq = jobIndex * step;
		String sql = "SELECT wordid, word from word_job_all j where j.wordid > "
				+ startSeq + " AND j.wordid <= " + (startSeq + step);
		// sql =
		// "SELECT wordid, word from word_job_all j where word = 'analogy'";
		System.out.println(sql);
		if (jobIndex > -1) {
			// return;
		}

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<Map<String, Object>> wordLst = new ArrayList<>();
		while (rs.next()) {
			Integer wordid = rs.getInt("wordid");
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
			System.err.println("job" + jobIndex + " find seq : " + count
					+ ", wordid is :" + wordid);
			System.out.println(word + ", start time is : " + new Date());
			long start = System.currentTimeMillis();
			handleWord(conn, wordid, word);
			long end = System.currentTimeMillis();
			System.out.println(word + ", consuming seconds : " + (end - start));
			count++;
		}

	}

	private static void handleWord(Connection conn, Integer wordid, String word)
			throws JSONException, SQLException, FileNotFoundException,
			IOException {
		String sql = "select * from langeasy.sentence s where s.type = 'orig' and s.text != '' and "
				+ "s.text REGEXP concat( '[[:<:]]', '" + word + "', '[[:>:]]')";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);

		List<Map<String, Object>> sentenceLst = new ArrayList<>();
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
		}
		rs.close();
		st.close();

		insertSentence(conn, sentenceLst);
	}

	private static Integer insertSentence(Connection conn,
			List<Map<String, Object>> sentenceLst) throws JSONException,
			SQLException {
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
