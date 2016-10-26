package com.chenyi.langeasy.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SentenceAudioLoader {
	private static Connection conn;

	public static void main(String args[]) {
		try {
			conn = SqliteHelper.getConnection("sentence-audio");

			int sentenceid = 255;
			// addAudio(sentenceid, "e:/langeasy/sentence/" + sentenceid +
			// ".mp3");
			listAudio();
			// listWord();
			// queryAudio(255);
			// queryPronAudio(21);

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public static void listAudio() {
		File dir = new File("e:/langeasy/sentence2_normalize");
		File[] files = dir.listFiles();
		for (File file : files) {
			String filename = file.getName();
			// System.out.println(filename);
			String sentenceid = filename.substring(0, filename.indexOf(".mp3"));
			System.out.println(sentenceid);
			// addAudio(Integer.parseInt(sentenceid), file);
			// break;
		}
	}

	public static void addAudio(int sentenceId, File file) {
		String isql = "insert into sentence_audio (sentenceid, audiodata) values (?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(isql)) {
			// set parameters
			pstmt.setInt(1, sentenceId);
			pstmt.setBytes(2, SqliteHelper.readFile(file));

			pstmt.executeUpdate();
			System.out.println("Stored the file in the BLOB column.");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
