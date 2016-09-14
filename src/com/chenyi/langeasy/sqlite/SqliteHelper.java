package com.chenyi.langeasy.sqlite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteHelper {
	private static Connection conn;

	public static void main(String args[]) {
		try {
			conn = getConnection(null);

			int sentenceid = 255;
			// addAudio(sentenceid, "e:/langeasy/sentence/" + sentenceid + ".mp3");
			// listAudio();
			// listWord();
			// queryAudio(255);
			queryPronAudio(21);

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public static Connection getConnection(String dbname) {
		Connection conn = null;
		if (dbname == null) {
			dbname = "langeasy";
		}
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:e:/langeasy/sqlite/" + dbname + ".db");
			System.out.println("Opened database successfully");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;
	}

	/**
	 * Read the file and returns the byte array
	 * 
	 * @param file
	 * @return the bytes of the file
	 */
	public static byte[] readFile(File file) {
		ByteArrayOutputStream bos = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			for (int len; (len = fis.read(buffer)) != -1;) {
				bos.write(buffer, 0, len);
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e2) {
			System.err.println(e2.getMessage());
		}
		return bos != null ? bos.toByteArray() : null;
	}

	public static void addPronAudio(int wordId, File file) {
		String isql = "insert into pron_audio (wordid, audiodata) values (?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(isql)) {
			// set parameters
			pstmt.setInt(1, wordId);
			pstmt.setBytes(2, readFile(file));

			pstmt.executeUpdate();
			System.out.println("Stored the file in the BLOB column.");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void queryAudio(int sentenceId) {
		String sql = "select audiodata from sentence_audio where sentenceid = ?";

		ResultSet rs = null;
		FileOutputStream fos = null;
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, sentenceId);
			rs = pstmt.executeQuery();

			// write binary stream into file
			File file = new File("e:/test26.mp3");
			fos = new FileOutputStream(file);

			System.out.println("Writing BLOB to file " + file.getAbsolutePath());
			while (rs.next()) {
				InputStream input = rs.getBinaryStream("audiodata");
				byte[] buffer = new byte[1024];
				while (input.read(buffer) > 0) {
					fos.write(buffer);
				}
			}
		} catch (SQLException | IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (fos != null) {
					fos.close();
				}

			} catch (SQLException | IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void queryPronAudio(int wordId) {
		String sql = "select audiodata from pron_audio where wordid = ?";

		ResultSet rs = null;
		FileOutputStream fos = null;
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, wordId);
			rs = pstmt.executeQuery();

			// write binary stream into file
			File file = new File("e:/test27.ogg");
			fos = new FileOutputStream(file);

			System.out.println("Writing BLOB to file " + file.getAbsolutePath());
			while (rs.next()) {
				InputStream input = rs.getBinaryStream("audiodata");
				byte[] buffer = new byte[1024];
				while (input.read(buffer) > 0) {
					fos.write(buffer);
				}
			}
		} catch (SQLException | IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (fos != null) {
					fos.close();
				}

			} catch (SQLException | IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
