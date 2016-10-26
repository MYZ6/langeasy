package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseCapture {
	private static CloseableHttpClient httpclient;
	private static Connection conn;

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		httpclient = HttpClients.createDefault();

		conn = CaptureUtil.getConnection();

		conn.setAutoCommit(false);

		listBook();

		// getBookInfo("1063472540");
		conn.close();

		httpclient.close();
	}

	private static void listBook() throws SQLException, FileNotFoundException, IOException {
		String sql = "select * from book where bookid not in (select bookid from course group by bookid)";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<String> booklst = new ArrayList<>();
		while (rs.next()) {
			String bookid = rs.getString("bookid");
			booklst.add(bookid);
		}
		rs.close();
		st.close();

		int count = 0;
		for (String bookid : booklst) {
			count++;
			System.err.println("find seq : " + count);

			getBookInfo(bookid);
		}
	}

	public static JSONObject getBookInfo2(String bookid) throws ClientProtocolException, IOException, JSONException,
			SQLException {
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

	public static void getBookInfo(String bookid) throws ClientProtocolException, IOException, JSONException,
			SQLException {
		String requestUrl = "http://langeasy.com.cn/getBookInfo.action?bookid=" + bookid
				+ "&courpage=1&courrows=3666&pubstate=1";
		HttpPost httppost = new HttpPost(requestUrl);

		CloseableHttpResponse response = httpclient.execute(httppost);
		// Get hold of the response entity
		HttpEntity entity = response.getEntity();

		byte[] result = null;
		// If the response does not enclose an entity, there is no need
		// to bother about connection release
		if (entity != null) {
			InputStream instream = entity.getContent();
			result = IOUtils.toByteArray(instream);
			instream.close();
		}
		response.close();

		String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		System.out.println(sResult);
		JSONObject book = null;
		try {
			book = new JSONObject(sResult);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sResult);
		}
		insertCourse(book.getJSONArray("courlist"));
	}

	private static Integer insertCourse(JSONArray courseLst) throws JSONException, SQLException {
		String insertSql = "INSERT INTO course (courseid, bookid, name, mp3path, ctime) VALUES(?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (int i = 0; i < courseLst.length(); i++) {
			JSONObject course = (JSONObject) courseLst.get(i);
			insPs.setString(1, course.getString("id"));
			insPs.setString(2, course.getString("bookid"));
			insPs.setString(3, course.getString("songname"));
			insPs.setString(4, course.getString("mp3path"));
			insPs.setTimestamp(5, new Timestamp(new Date().getTime()));
			insPs.addBatch();
		}

		insPs.executeBatch();
		conn.commit();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
