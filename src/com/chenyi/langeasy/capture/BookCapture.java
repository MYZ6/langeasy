package com.chenyi.langeasy.capture;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

public class BookCapture {

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
		conn.close();
	}

	public static JSONObject getBookInfo(CloseableHttpClient httpclient, String bookid)
			throws ClientProtocolException, IOException {
		String requestUrl = "http://langeasy.com.cn/getBookInfo.action?bookid=" + bookid
				+ "&courpage=1&courrows=1666&pubstate=1";
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
}
