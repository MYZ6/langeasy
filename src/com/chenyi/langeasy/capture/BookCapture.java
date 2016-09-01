package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookCapture {

	private static CloseableHttpClient httpclient;

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		httpclient = HttpClients.createDefault();

		// JSONObject book = getBookInfo("1688236492");

		Connection conn = CaptureUtil.getConnection();
		// insertBook(conn, book);
		listBook(conn);

		conn.close();
		httpclient.close();
	}

	private static void listBook(Connection conn) throws SQLException, FileNotFoundException, IOException {
		// String requestUrl =
		// "http://langeasy.com.cn/newlisten/bookResult.action?bookcells=86&bookpage=1&lrctype=&sort=1&type=%E7%BE%8E%E5%89%A7%26%E8%8B%B1%E5%89%A7";
		// HttpPost httppost = new HttpPost(requestUrl);
		//
		// CloseableHttpResponse response = httpclient.execute(httppost);
		// // Get hold of the response entity
		// HttpEntity entity = response.getEntity();
		//
		// byte[] result = null;
		// // If the response does not enclose an entity, there is no need
		// // to bother about connection release
		// if (entity != null) {
		// InputStream instream = entity.getContent();
		// result = IOUtils.toByteArray(instream);
		// instream.close();
		// }
		// response.close();

		File bookFile = new File("E:\\booklist.json");
		String sResult = IOUtils.toString(new FileInputStream(bookFile), "utf-8");

		// String sResult = new String(result, "utf-8");
		sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		JSONArray booklist = null;
		try {
			JSONObject json = new JSONObject(sResult);
			booklist = json.getJSONArray("booklist");
			System.out.println(booklist.length());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sResult);
		}

		insertBook(conn, booklist);
	}

	private static Integer insertBook(Connection conn, JSONArray booklist) throws JSONException, SQLException {
		String insertSql = "INSERT INTO book (bookid, bookname, booktype, detail, coverpath) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		for (int i = 0; i < booklist.length(); i++) {
			JSONObject book = booklist.getJSONObject(i);
			insPs.setString(1, book.getString("bookid"));
			insPs.setString(2, book.getString("bookname"));
			insPs.setString(3, "美剧&英剧");
			insPs.setString(4, "");
			insPs.setString(5, book.getString("imagepath"));
			insPs.addBatch();
		}

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();

		return 0;
	}
}
