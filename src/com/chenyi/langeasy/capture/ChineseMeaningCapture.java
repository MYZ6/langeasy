package com.chenyi.langeasy.capture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChineseMeaningCapture {
	public static void main(String[] args) throws IOException, JSONException,
			SQLException {
		String word = "abaft";
		String url = "http://fanyi.baidu.com/v2transapi?from=en&query=" + word
				+ "&simple_means_flag=3&to=zh";
		// Document doc = Jsoup.connect(url).get();
		// System.out.println(doc.html());

		// File courseFile = new File("E:\\dictionary.html");
		// String sResult = IOUtils.toString(new FileInputStream(courseFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		Connection conn = CaptureUtil.getConnection();
		listWord(conn);
		CaptureUtil.closeConnection(conn);
	}

	static List<Map<String, Object>> wordLst = null;

	private static List<Map<String, String>> listWord(Connection conn)
			throws JSONException, SQLException, FileNotFoundException,
			IOException {
		// String sql = "SELECT id, word from vocabulary where pron is null";
		String sql = "SELECT id, word from vocabulary where 1=1 and meaning is null";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		wordLst = new ArrayList<>();
		while (rs.next()) {
			Integer wordid = rs.getInt("id");
			String word = rs.getString("word");
			Map<String, Object> wordMap = new HashMap<>();
			wordMap.put("wordid", wordid);
			wordMap.put("word", word);
			wordLst.add(wordMap);
		}
		rs.close();
		st.close();

		System.out.println(wordLst.size());
		ChineseMeaningCapture downloader = new ChineseMeaningCapture();
		for (int i = 0; i < 2; i++) {
			Job job = downloader.new Job(i);
			job.start();
		}

		return null;
	}

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			Connection conn = CaptureUtil.getConnection();
			int step = 1;
			int start = jobIndex * step;
			List<Map<String, Object>> subLst = wordLst.subList(start, start
					+ step);
			int count = 0;
			CloseableHttpClient httpclient = HttpClients.createDefault();
			for (Map<String, Object> map : subLst) {
				count++;
				System.err
						.println("job" + jobIndex + " request seq : " + count);
				try {
					handleWord(httpclient, conn, (int) map.get("wordid"),
							(String) map.get("word"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			CaptureUtil.closeConnection(conn);
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}

	private static void handleWord(CloseableHttpClient httpclient,
			Connection conn, Integer wordid, String word) throws JSONException,
			SQLException, FileNotFoundException, IOException {
		String url = "http://fanyi.baidu.com/v2transapi?from=en&query=" + word
				+ "&simple_means_flag=3&to=zh";
		System.out.println(url);
		// url =
		// "https://www.oxforddictionaries.com/definition/english/a-la-carte?q=%C3%A0+la+carte";
		HttpPost httppost = new HttpPost(url);

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
		// System.out.println(sResult);
		JSONObject json = null;
		try {
			json = new JSONObject(sResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(json);
		JSONObject trans_result = json.getJSONObject("trans_result");
		JSONArray data = trans_result.getJSONArray("data");
		JSONObject data1 = data.getJSONObject(0);
		String dst = data1.getString("dst");
		System.out.println(dst);
		// int wordId = getWordId(conn, word);
		updateWordMeaning(conn, wordid, dst);

	}

	private static void updateWordMeaning(Connection conn, int wordId,
			String meaning) throws JSONException, SQLException,
			FileNotFoundException, IOException {
		String usql = "update vocabulary set meaning = ? where id = ?";
		PreparedStatement insPs = conn.prepareStatement(usql);
		insPs.setString(1, meaning);
		insPs.setInt(2, wordId);
		insPs.addBatch();

		insPs.executeBatch();
		insPs.clearBatch();
		insPs.close();
	}
}
