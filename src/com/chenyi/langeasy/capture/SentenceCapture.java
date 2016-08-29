package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SentenceCapture {
	public static void main(String[] args) throws IOException, JSONException, SQLException {
		String courseid = "20120727135500100000036";
		String url = "http://langeasy.com.cn/m/player?f=" + courseid;
		// Document doc = Jsoup.connect(url).get();

		File courseFile = new File("E:\\sentence.html");
		String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
		Document doc = Jsoup.parse(sResult);
		Elements liArr = doc.select("#lrcContent li");

		insertSentence(DBConfig.getConnection(), courseid, liArr);
	}

	private static Integer insertSentence(Connection conn, String courseid, Elements liArr)
			throws JSONException, SQLException {
		String insertSql = "INSERT INTO sentence (courseid, type, dataindex, encryptstarttime, text) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (Element li : liArr) {
			String type = li.attr("class");
			String dataIndex = li.attr("data-index");
			String encryptstarttime = li.attr("id");
			String text = li.text();

			insPs.setString(1, courseid);
			insPs.setString(2, type);
			insPs.setInt(3, Integer.parseInt(dataIndex));
			insPs.setString(4, encryptstarttime);
			insPs.setString(5, text);
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
