package com.chenyi.langeasy.capture.podcast.money;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class TranscriptCapture {
	static List<JSONObject> downloadLst = new ArrayList<>();

	private static String dirPath = "E:/langeasy/lucene/podcast/money/";

	public static void main(String[] args) throws Exception {
		System.out.println("start time is : " + new Date());

		File sFile = new File(dirPath + "episode-list.json");
		// sFile = new File(dirPath + "test.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		// String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		JSONArray episodeList = new JSONArray(sResult);
		for (int i = 0; i < episodeList.length(); i++) {
			JSONObject episode = episodeList.getJSONObject(i);
			downloadLst.add(episode);
		}

		int total = downloadLst.size();
		System.out.println(total);
		if (total > -1) {
			// return;
		}
		step = 7;
		int count = total / step;
		if (total % step != 0) {
			count += 1;
		}
		TranscriptCapture downloader = new TranscriptCapture();
		for (int i = 0; i < count; i++) {
			Job job = downloader.new Job(i);
			job.start();
			if (i > 2) {
				// return;
			}
		}
	}

	private static int step = 30;

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			int start = jobIndex * step;
			int end = start + step;
			if (end > downloadLst.size()) {
				end = downloadLst.size();
			}
			System.out.println(start + "\t" + end);

			List<JSONObject> subLst = downloadLst.subList(start, end);
			int count = 0;
			for (JSONObject story : subLst) {
				count++;
				if (count % 2 == 1) {
					try {
						httpclient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					httpclient = HttpClients.createDefault();
				}
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					getTranscript(httpclient, story.getString("eid"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 1) {
					// break;
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("job" + jobIndex + " last time is : " + new Date());
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}

	private static void getTranscript(CloseableHttpClient httpclient, String storyId) throws Exception {
		System.out.println(storyId + ", start time is : " + new Date());
		long start = System.currentTimeMillis();

		File saveFile = new File(dirPath + File.separator + "transcript" + File.separator + storyId + ".html");
		if (saveFile.exists()) {
			return;
		}

		String url = "http://www.npr.org/templates/transcript/transcript.php?storyId=" + storyId;
		HttpGet httpget = new HttpGet(url);

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			long startTime = System.currentTimeMillis();
			System.out.println(storyId + " download begin");
			InputStream inputStream = entity.getContent();
			OutputStream outputStream = new FileOutputStream(saveFile);
			IOUtils.copy(inputStream, outputStream);
			outputStream.close();
			System.out.println(storyId + " download success");
			long endTime = System.currentTimeMillis();
			System.out.println("download time elapsed: " + (endTime - startTime));
		}
		long end = System.currentTimeMillis();
		System.out.println(storyId + ", consuming time : " + (end - start));

	}
}
