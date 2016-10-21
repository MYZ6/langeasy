package com.chenyi.langeasy.sqlite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NumberAudioLoader {
	private static Connection conn;

	public static void main(String[] args) {
		try {
			conn = SqliteHelper.getConnection("number-audio");

			listWord();

			conn.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	private static void listWord() throws SQLException, FileNotFoundException,
			IOException {

		String isql = "insert into number_audio (num, audiodata) values (?, ?)";

		PreparedStatement insPs = conn.prepareStatement(isql);
		for (int i = 0; i < 1500; i++) {
			System.out.println("find seq : " + i);

			String filepath = "e:/langeasy/number_normalize/" + i + ".mp3";
			File saveFile = new File(filepath);

			// set parameters
			insPs.setInt(1, i);
			insPs.setBytes(2, SqliteHelper.readFile(saveFile));
			insPs.addBatch();

			if (i % 99 == 96) {
				System.out.println("\texecute times : " + i / 99);
				try {
					insPs.executeBatch();
				} catch (BatchUpdateException exception) {
					exception.printStackTrace();
					String message = exception.getMessage();
					System.out.println(message);
					if (message.indexOf("primary") > -1) {
						System.out.println(i + " is alreay exist.");
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
				System.out.println("number is alreay exist.");
			} else {
				throw exception;
			}
		}
		insPs.clearBatch();
		insPs.close();

	}
}
