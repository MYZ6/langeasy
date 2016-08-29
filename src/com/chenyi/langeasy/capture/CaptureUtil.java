package com.chenyi.langeasy.capture;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CaptureUtil {

	static final String DB_URL = "jdbc:mysql://localhost:3306/langeasy";
	static final String USER = "root";
	static final String PASS = "";

	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return conn;

	}

	public static Document timeoutRequest(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (SocketTimeoutException ex) {
			System.out.println("url " + url + " read timeout");
			ex.printStackTrace();
			doc = timeoutRequest(url);// try again recursively
			// throw ex;
		} catch (HttpStatusException ex) {
			if (404 == ex.getStatusCode()) {
				System.out.println("url " + url + " 404");
				ex.printStackTrace();
			}
			// throw ex;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
}
