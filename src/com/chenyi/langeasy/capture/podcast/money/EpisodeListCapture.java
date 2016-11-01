package com.chenyi.langeasy.capture.podcast.money;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class EpisodeListCapture {
	private static String dirPath = "E:/langeasy/lucene/podcast/money/";

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		// archive(1);
		// System.out.println(new JSONArray(episodeLst).toString(3));
		System.out.println("start time is : " + new Date());

		EpisodeListCapture downloader = new EpisodeListCapture();
		int count = 213;
		int start = 0;
		jobStatus = new int[count];
		for (int i = start; i < count; i++) {
			if (i > -1) {
				// return;
			}
			jobStatus[i] = 0;
			Job job = downloader.new Job(i);
			job.start();
			if (i % 10 == 9) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// return;
			}
		}
		for (int i = 0; i < 20; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean allFinished = true;
			for (int j = start; j < count; j++) {
				if (jobStatus[j] == 0) {
					allFinished = false;
					break;
				}
			}
			if (allFinished) {
				File sFile = new File(dirPath + "episode-list2.json");
				FileUtils.writeStringToFile(sFile, new JSONArray(episodeLst).toString(3), StandardCharsets.UTF_8);
				System.out.println("end time is : " + new Date());
				break;
			}
		}

	}

	private static int[] jobStatus;

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			try {
				archive(jobIndex * 15 + 1);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
			jobStatus[jobIndex] = 1;
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}

	static List<Map<String, String>> episodeLst = new ArrayList<>();

	public static void archive(int start) throws FileNotFoundException, IOException, ParseException {
		String url = "http://www.npr.org/sections/money/archive?start=" + start + "&date=2010-10-28";
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url);

		// File htmlFile = new
		// File("E:/langeasy/lucene/podcast/money/archive.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Elements liArr = doc.select(".archivelist .item");

		int count = 0;
		for (Element li : liArr) {
			count++;

			String title = li.select(".title").get(0).text();

			Elements downloadBtn = li.select(".audio-tool-download a");
			if (downloadBtn.size() == 0) {
				System.out.println(count + "/" + title + " has no download");
				continue;
			}
			Elements transcriptBtn = li.select(".audio-tool-transcript a");
			if (transcriptBtn.size() == 0) {
				System.out.println(count + "/" + title + " has no transcript");
				continue;
			}

			String data = downloadBtn.get(0).attr("data-metrics");
			JSONObject json = new JSONObject(data);
			String storyId = json.getString("label");

			String edate = li.select("time").get(0).attr("datetime");
			String teaser = li.select(".teaser").get(0).text();
			String slug = li.select(".slug").get(0).text();

			Map<String, String> map = new HashMap<>();
			map.put("eid", storyId);
			map.put("edate", edate);
			map.put("slug", slug);
			map.put("title", title);
			map.put("teaser", teaser);
			episodeLst.add(map);
		}
	}
}
