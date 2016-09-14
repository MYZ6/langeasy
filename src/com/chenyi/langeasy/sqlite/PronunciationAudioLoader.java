package com.chenyi.langeasy.sqlite;

import java.io.File;
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

public class PronunciationAudioLoader {
	private static Connection conn;

	public static void main(String[] args) {
		try {
			conn = SqliteHelper.getConnection("pron-audio");

			listWord();

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	private static void listWord() throws SQLException, FileNotFoundException, IOException {
		//		String sql = "SELECT id, oggpath FROM vocabulary where oggpath is not null limit 10000";
		String sql = "SELECT id, oggpath FROM vocabulary where dfrom is not null and oggpath is not null limit 10000";
		Connection mysqlConn = CaptureUtil.getConnection();
		Statement st = mysqlConn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<Map<String, Object>> recordLst = new ArrayList<>();
		while (rs.next()) {
			Integer wordid = rs.getInt("id");
			String oggpath = rs.getString("oggpath");
			Map<String, Object> map = new HashMap<>();
			map.put("wordid", wordid);
			map.put("oggpath", oggpath);
			recordLst.add(map);// handle as mp3
		}
		rs.close();
		st.close();
		mysqlConn.close();

		String prefix = "https://www.oxforddictionaries.com/media/english/";

		String isql = "insert into pron_audio (wordid, audiodata) values (?, ?)";

		PreparedStatement insPs = conn.prepareStatement(isql);
		int count = 0;
		for (Map<String, Object> map : recordLst) {
			count++;
			System.out.println("find seq : " + count);

			Integer wordid = (Integer) map.get("wordid");
			String mediaUrl = (String) map.get("oggpath");
			System.out.println(mediaUrl);
			String localMediapath = mediaUrl.substring(prefix.length());
			System.out.println(localMediapath);
			String filepath = "e:/langeasy/pronunciation/" + localMediapath;// "ListenData/1688236492/2089837271.mp3";
			File saveFile = new File(filepath);

			// set parameters
			insPs.setInt(1, wordid);
			insPs.setBytes(2, SqliteHelper.readFile(saveFile));
			insPs.addBatch();

			if (count % 99 == 96) {
				System.out.println("\texecute times : " + count / 99);
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
