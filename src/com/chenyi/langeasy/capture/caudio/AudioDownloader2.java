package com.chenyi.langeasy.capture.caudio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
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

public class AudioDownloader2 {
	private static CloseableHttpClient httpclient;

	private static List<Map<String, String>> downloadLst;

	public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
		httpclient = HttpClients.createDefault();
		System.out.println("start time is : " + new Date());
		listCourse();
		// httpclient.close();
	}

	private static void listCourse() throws SQLException, FileNotFoundException, IOException {
		String[] fileArr = new String[] { "ListenData/269296427/1684575709.mp3", "ListenData/1404568722/1415012900.mp3",
				"ListenData/977915444/2054856991.mp3", "ListenData/538418611/216561888.mp3",
				"ListenData/100279329/1230981771.mp3", "ListenData/349559752/1919249301.mp3",
				"ListenData/538418611/1235380786.mp3", "ListenData/1694965458/1154810428.mp3",
				"ListenData/349559752/2036962820.mp3", "ListenData/1694965458/376064581.mp3",
				"ListenData/538418611/1600531794.mp3", "ListenData/1983179718/680991519.mp3",
				"ListenData/134132630/592709876.mp3", "ListenData/915900259/1952231911.mp3",
				"ListenData/270963162/1379157890.mp3" };
		int count = 0;
		downloadLst = new ArrayList<>();
		for (String mp3path : fileArr) {
			count++;
			System.out.println("find seq : " + count);
			String filepath = "e:/langeasy/" + mp3path;// "ListenData/1688236492/2089837271.mp3";
			File saveFile = new File(filepath);
			if (saveFile.exists()) {
				// continue;
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
		if (total > -1) {
			// return;
		}

		AudioDownloader2 downloader = new AudioDownloader2();
		for (int i = 0; i < 15; i++) {
			Job job = downloader.new Job(i);
			job.start();
		}

	}

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			int step = 1;
			int start = jobIndex * step;
			System.out.println(start + "\t" + (start + step));
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
