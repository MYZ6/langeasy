package com.chenyi.langeasy.capture;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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

	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Document timeoutRequest(String url) {
		Document doc = null;
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";
		try {
			doc = Jsoup.connect(url).userAgent(userAgent).get();
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
			if (403 == ex.getStatusCode()) {
				System.err.println("url " + url + " 403");
				ex.printStackTrace();
			}
			// throw ex;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	public static Integer decodeTime(String timestr) {
		long startTime = System.currentTimeMillis();
		String salt = "8ABC7DLO5MN6Z9EFGd";
		String newSalt = salt + "eJfghijkHIVrstuvwWSTUXYabclmnopqKPQRxyz01234";
		String startDigit = timestr.substring(0, 1);
		// System.out.println(startDigit);
		// if (isNaN(i)) return "";
		String secondSub = timestr.substring(1, 1 + Integer.parseInt(startDigit));
		// System.out.println(secondSub);
		int nSecond = Integer.parseInt(secondSub);
		// if (i = 1 * t.substr(1, i), isNaN(i)) return "";
		int o, r;
		int timestrLength = timestr.length();
		List<String> codeArr = new ArrayList<>();
		int secondSubLength = secondSub.length() + 1;

		// var o, r, s = t.length,
		// a = [],//
		// l = String(i).length + 1,//secondSubLength
		// c = function(e) {
		// return n.indexOf(t.charAt(e))
		// },
		int newSaltLength = newSalt.length();

		// d = n.length;//newSaltLength
		if (timestrLength != secondSubLength + nSecond) {
			return null;
		}
		// if (s != l + i) return "";
		for (; timestrLength > secondSubLength;) {
			o = saltIndex(newSalt, timestr, secondSubLength++);
			r = 5 > o ? o * newSaltLength + saltIndex(newSalt, timestr, secondSubLength) : (o - 5) * newSaltLength
					* newSaltLength + saltIndex(newSalt, timestr, secondSubLength += 1);
			char[] chars = Character.toChars(r);
			codeArr.add(new String(chars));
			secondSubLength++;
		}
		// for (; s > l;) o = c(l++), r = 5 > o ? o * d + c(l) : (o - 5) * d * d
		// + c(l) * d + c(l += 1),
		// a[a.length] = String.fromCharCode(r), l++;
		// return a.join("")
		String decodeTime = StringUtils.join(codeArr, "");
		String[] timeArr = decodeTime.split("[a-zA-Z]");
		// System.out.println(decodeTime);
		String finalTime = timeArr[1];
		// System.out.println(finalTime);
		long endTime = System.currentTimeMillis();
		// System.out.println("decode operate elapsed: " + (endTime -
		// startTime));
		return Integer.parseInt(finalTime);
	}

	public static int saltIndex(String newSalt, String timestr, int index) {
		String sub = timestr.substring(index, index + 1);
		int saltIndex = newSalt.indexOf(sub);
		return saltIndex;
	}

	public static void main(String[] args) {
		int decodeTime = decodeTime("2148xAX8P808y808q");
	}

	// s52d: function(t, e) {
	// var n = e + "eJfghijkHIVrstuvwWSTUXYabclmnopqKPQRxyz01234",
	// i = 1 * t.charAt(0);
	// if (isNaN(i)) return "";
	// if (i = 1 * t.substr(1, i), isNaN(i)) return "";
	// var o, r, s = t.length,
	// a = [],
	// l = String(i).length + 1,
	// c = function(e) {
	// return n.indexOf(t.charAt(e))
	// },
	// d = n.length;
	// if (s != l + i) return "";
	// for (; s > l;) o = c(l++), r = 5 > o ? o * d + c(l) : (o - 5) * d * d +
	// c(l) * d + c(l += 1), a[a.length] =
	// String.fromCharCode(r), l++;
	// return a.join("")
	// },
}
