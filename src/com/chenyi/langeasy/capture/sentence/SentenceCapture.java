package com.chenyi.langeasy.capture.sentence;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class SentenceCapture {
	private static Connection conn;

	public static void main(String[] args) throws Exception {
		conn = CaptureUtil.getConnection();
		listCourse(conn);
		CaptureUtil.closeConnection(conn);
	}

	private static List<Map<String, String>> listCourse(Connection conn) throws Exception {
		String sql = "SELECT c.courseid FROM book b inner join course c on c.bookid = b.bookid left join sentence s on s.courseid = c.courseid "
				+ "where 1=1 and s.courseid is null group by c.courseid ";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);

		List<String> courseidLst = new ArrayList<>();
		while (rs.next()) {
			String courseid = rs.getString("courseid");
			courseidLst.add(courseid);
			// courseid = "20120727135500100000028";

		}
		rs.close();
		st.close();

		int count = 0;
		for (String courseid : courseidLst) {
			count++;
			System.err.println("find seq : " + count);
			parseCourse(courseid);
			// break;
		}

		return null;
	}

	private static void parseCourse(String courseid) throws Exception {
		String url = "http://langeasy.com.cn/m/player?f=" + courseid;
		Document doc = CaptureUtil.timeoutRequest(url);

		// File courseFile = new File("E:\\sentence.html");
		// String sResult = IOUtils.toString(new FileInputStream(courseFile), "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Elements liArr = doc.select("#lrcContent li");

		List<Map<String, Object>> sentenceLst = new ArrayList<>();
		for (Element li : liArr) {
			String type = li.attr("class");
			String dataIndex = li.attr("data-index");
			String encryptstarttime = li.attr("id");
			int decodestarttime = 0;
			if ("".equals(encryptstarttime)) {
				continue;
			} else {
				decodestarttime = CaptureUtil.decodeTime(encryptstarttime);
			}
			String text = li.text();

			Map<String, Object> map = new HashMap<>();
			map.put("dataIndex", Integer.parseInt(dataIndex));
			map.put("type", type);
			map.put("text", text);
			// System.out.println(text);
			map.put("decodestarttime", decodestarttime);
			sentenceLst.add(map);
		}
		// calculate end time
		for (int i = 0; i < sentenceLst.size(); i++) {
			Map<String, Object> sentence = sentenceLst.get(i);
			String type = (String) sentence.get("type");
			Integer dataIndex = (Integer) sentence.get("dataIndex");
			Integer endTime = 0;
			if (i + 2 <= sentenceLst.size() - 1) {
				Map<String, Object> nextSentence = sentenceLst.get(i + 2);

				Integer nextIndex = (Integer) nextSentence.get("dataIndex");
				String nextType = (String) sentence.get("type");
				if (dataIndex + 1 != nextIndex || !nextType.equals(type)) {
					throw new Exception("find endtime error, text is :" + sentence.get("text"));
				}
				endTime = (Integer) nextSentence.get("decodestarttime");
			}
			sentence.put("endtime", endTime);
		}
		System.out.println(courseid + ", size: " + sentenceLst.size());
		insertSentence(courseid, sentenceLst);
		// System.out.println(sentenceLst);

	}

	private static Integer insertSentence(String courseid, List<Map<String, Object>> sentenceLst)
			throws JSONException, SQLException {
		String insertSql = "INSERT INTO sentence (courseid, type, dataindex, text, decodestarttime, endtime) VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement insPs = conn.prepareStatement(insertSql);
		int count = 0;
		for (Map<String, Object> sentence : sentenceLst) {
			String type = (String) sentence.get("type");
			Integer dataIndex = (Integer) sentence.get("dataIndex");
			Integer decodestarttime = (Integer) sentence.get("decodestarttime");
			Integer endtime = (Integer) sentence.get("endtime");
			String text = (String) sentence.get("text");

			insPs.setString(1, courseid);
			insPs.setString(2, type);
			insPs.setInt(3, dataIndex);
			insPs.setString(4, text);
			insPs.setInt(5, decodestarttime);
			insPs.setInt(6, endtime);
			insPs.addBatch();

			if (count % 199 == 99) {
				System.out.println("\texecute times : " + count / 199);
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
			}

			count++;
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
