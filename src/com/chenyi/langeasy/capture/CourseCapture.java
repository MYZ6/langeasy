package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseCapture {

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<JSONObject> allCarLst = new ArrayList<>();
		// subidArr = new String[] { "2124" };
		int count = 0;
		// for (String subid : subidArr) {
		System.out.println(allCarLst.size());

		JSONObject book = getBookInfo(httpclient, "1688236492");

		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

		// insertBook(conn, book);
		insertCourse(conn, book.getJSONArray("courlist"));
		conn.close();
	}

	public static JSONObject getBookInfo(CloseableHttpClient httpclient, String bookid)
			throws ClientProtocolException, IOException {
		File courseFile = new File("E:\\course.json");
		String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
		sResult = StringEscapeUtils.unescapeJava(sResult);
		System.out.println(sResult);
		JSONObject book = null;
		try {
			book = new JSONObject(sResult);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sResult);
		}
		return book;
	}

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/langeasy";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "";

	private static Integer insertBook(Connection conn, JSONObject book) throws JSONException, SQLException {
		String insertSql = "INSERT INTO book (bookid, bookname, booktype, detail, coverpath) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		insPs.setString(1, book.getString("bookid"));
		insPs.setString(2, book.getString("bookname"));
		insPs.setString(3, book.getString("booktype"));
		insPs.setString(4, book.getString("detail"));
		insPs.setString(5, book.getString("coverpath"));
		insPs.addBatch();

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}

	private static Integer insertCourse(Connection conn, JSONArray courseLst) throws JSONException, SQLException {
		String insertSql = "INSERT INTO course (courseid, bookid, name, mp3path) VALUES(?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (int i = 0; i < courseLst.length(); i++) {
			JSONObject course = (JSONObject) courseLst.get(i);
			insPs.setString(1, course.getString("id"));
			insPs.setString(2, course.getString("bookid"));
			insPs.setString(3, course.getString("songname"));
			insPs.setString(4, course.getString("mp3path"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
