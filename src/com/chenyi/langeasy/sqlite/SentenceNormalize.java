package com.chenyi.langeasy.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class SentenceNormalize {
	private static Connection conn;

	public static void main(String[] args) {
		System.out.println("start time is : " + new Date());
		try {
			conn = SqliteHelper.getConnection("sentence-audio");

			//			listAudio();

			int[] sidArr = new int[] { 391695, 456369, 623331, 168613, 231711, 412125 };
			listAudio(sidArr);

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	public static void listAudio(int[] idArr) {
		String dirpath = "e:/langeasy/sentence_normalize";
		for (int sentenceid : idArr) {
			String filename = dirpath + File.separator + sentenceid + ".mp3";
			System.out.println(filename);
			updateAudio(sentenceid, new File(filename));
			//			break;
		}
	}

	public static void listAudio() {
		File dir = new File("e:/langeasy/sentence_normalize");
		File[] files = dir.listFiles();
		for (File file : files) {
			String filename = file.getName();
			System.out.println(filename);
			String sentenceid = filename.substring(0, filename.indexOf(".mp3"));
			updateAudio(Integer.parseInt(sentenceid), file);
			// break;
		}
	}

	public static void updateAudio(int sentenceId, File file) {
		String isql = "update sentence_audio set audiodata = ?, mtime = ? where sentenceid = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(isql)) {
			// set parameters
			pstmt.setBytes(1, SqliteHelper.readFile(file));
			pstmt.setDate(2, new java.sql.Date(new Date().getTime()));
			pstmt.setInt(3, sentenceId);

			pstmt.executeUpdate();
			System.out.println("update " + sentenceId);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
