package com.chenyi.langeasy.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class AudioDownloader {
	private static CloseableHttpClient httpclient;

	private static List<Map<String, String>> downloadLst;

	public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
		httpclient = HttpClients.createDefault();
		Connection conn = CaptureUtil.getConnection();
		System.out.println("start time is : " + new Date());
		listCourse(conn);
		conn.close();
		// httpclient.close();
	}

	private static void listCourse(Connection conn) throws SQLException, FileNotFoundException, IOException {
		String sql = "SELECT mp3path FROM langeasy.course c WHERE c.courseid IN "
				+ "( SELECT s.courseid FROM langeasy.vocabulary_audio r "
				+ "INNER JOIN sentence s ON s.id = r.sentenceid GROUP BY s.courseid)" + "limit 5000";

		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		List<String> recordLst = new ArrayList<>();
		while (rs.next()) {
			String mp3path = rs.getString("mp3path");
			recordLst.add(mp3path);
		}
		rs.close();
		st.close();

		int count = 0;
		downloadLst = new ArrayList<>();
		for (String mp3path : recordLst) {
			count++;
			System.out.println("find seq : " + count);
			String filepath = "e:/langeasy/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
			File saveFile = new File(filepath);
			if (saveFile.exists()) {
				continue;
			}
			String dirpath = saveFile.getParent();
			File dir = new File(dirpath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			System.out.println(dirpath);

			Map<String, String> map = new HashMap<>();
			map.put("mp3path", mp3path);
			map.put("saveFilePath", filepath);
			downloadLst.add(map);
			// downloadMp3(mp3path, saveFile);
		}
		int total = downloadLst.size();// about 1800
		System.out.println(total);
		int step = 30;

		// AudioDownloader downloader = new AudioDownloader();
		// for (int i = 0; i < 13; i++) {
		// Job job = downloader.new Job(i);
		// job.start();
		// }

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
			List<Map<String, String>> subLst = downloadLst.subList(start, start + step);
			int count = 0;
			for (Map<String, String> map : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					downloadMp3(map.get("mp3path"), map.get("saveFilePath"));
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

	private static void downloadMp3(String mp3path, String saveFilePath) throws ClientProtocolException, IOException {
		System.out.println(saveFilePath + ", start time is : " + new Date());
		long start = System.currentTimeMillis();

		File saveFile = new File(saveFilePath);

		String url = "http://langeasy.com.cn/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
		HttpGet httpget = new HttpGet(url);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			long startTime = System.currentTimeMillis();
			System.out.println(mp3path + " download begin");
			InputStream inputStream = entity.getContent();
			OutputStream outputStream = new FileOutputStream(saveFile);
			IOUtils.copy(inputStream, outputStream);
			outputStream.close();
			System.out.println(mp3path + " download success");
			long endTime = System.currentTimeMillis();
			System.out.println("download time elapsed: " + (endTime - startTime));
		}
		httpclient.close();
		long end = System.currentTimeMillis();
		System.out.println(saveFilePath + ", consuming seconds : " + (end - start));

	}
}
