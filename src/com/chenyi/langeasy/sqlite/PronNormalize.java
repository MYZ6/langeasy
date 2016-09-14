package com.chenyi.langeasy.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class PronNormalize {
	private static Connection conn;

	public static void main(String[] args) {
		System.out.println("start time is : " + new Date());
		try {
			conn = SqliteHelper.getConnection("pron-audio");

			listAudio();

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	public static void listAudio() {
		File dir = new File("e:/langeasy/pronunciation-normalize");
		File[] files = dir.listFiles();
		for (File file : files) {
			String filename = file.getName();
			System.out.println(filename);
			String wordid = filename.substring(0, filename.indexOf(".ogg"));
			updateAudio(Integer.parseInt(wordid), file);
			// break;
		}
	}

	public static void updateAudio(int wordid, File file) {
		String isql = "update pron_audio set audiodata = ? where wordid = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(isql)) {
			// set parameters
			pstmt.setBytes(1, SqliteHelper.readFile(file));
			pstmt.setInt(2, wordid);

			pstmt.executeUpdate();
			System.out.println("update " + wordid);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
