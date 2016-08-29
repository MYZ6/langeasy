package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class VocabularyHandler2 {
	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
		File courseFile = new File("E:\\vocabulary2.txt");
		String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
		// System.out.println(sResult);

		Pattern pattern = Pattern.compile("[a-zA-Z\\-]+\\b");
		Matcher matcher = pattern.matcher(sResult);
		int count = 0;

		Connection conn = CaptureUtil.getConnection();
		String insertSql = "INSERT INTO vocabulary (word, type) VALUES(?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		while (matcher.find()) {
			String word = matcher.group(0).toLowerCase();
			System.out.println(word + "\t" + count);
			insPs.setString(1, word);
			insPs.setString(2, "gmat");
			insPs.addBatch();
			count++;
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

	}

}
