package com.chenyi.langeasy.capture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SentenceCapture {
	public static void main(String[] args) throws IOException, JSONException, SQLException {
		String courseid = "20120727135500100000036";
		String url = "http://langeasy.com.cn/m/player?f=" + courseid;
		// Document doc = Jsoup.connect(url).get();

		// File courseFile = new File("E:\\sentence.html");
		// String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
		// Document doc = Jsoup.parse(sResult);
		// Elements liArr = doc.select("#lrcContent li");

		Connection conn = CaptureUtil.getConnection();
		listCourse(conn);
		CaptureUtil.closeConnection(conn);
	}

	private static List<Map<String, String>> listCourse(Connection conn)
			throws JSONException, SQLException, FileNotFoundException, IOException {
		String sql = "SELECT c.courseid FROM course c left join sentence s on s.courseid = c.courseid "
				+ "where s.courseid is null group by c.courseid ";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		while (rs.next()) {
			String courseid = rs.getString("courseid");
			// courseid = "20120727135500100000028";

			String url = "http://langeasy.com.cn/m/player?f=" + courseid;
			Document doc = CaptureUtil.timeoutRequest(url);

			// File courseFile = new File("E:\\sentence.html");
			// String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
			// Document doc = Jsoup.parse(sResult);

			Elements liArr = doc.select("#lrcContent li");

			insertSentence(conn, courseid, liArr);
			System.out.println(courseid);

			// break;
		}
		rs.close();
		st.close();

		return null;
	}

	private static Integer insertSentence(Connection conn, String courseid, Elements liArr)
			throws JSONException, SQLException {
		String insertSql = "INSERT INTO sentence (courseid, type, dataindex, encryptstarttime, text) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (Element li : liArr) {
			String type = li.attr("class");
			String dataIndex = li.attr("data-index");
			String encryptstarttime = li.attr("id");
			if ("".equals(encryptstarttime)) {
				continue;
			}
			String text = li.text();

			insPs.setString(1, courseid);
			insPs.setString(2, type);
			insPs.setInt(3, Integer.parseInt(dataIndex));
			insPs.setString(4, encryptstarttime);
			insPs.setString(5, text);
			insPs.addBatch();
		}
		try {
			insPs.executeBatch();
		} catch (BatchUpdateException exception) {
			exception.printStackTrace();
			String message = exception.getMessage();
			System.out.println(message);
			if (message.indexOf("sentence_unique") > -1) {
				System.out.println(courseid + " is alreay exist.");
			} else {
				throw exception;
			}
		}
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
