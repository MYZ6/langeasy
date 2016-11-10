package com.chenyi.langeasy.capture.podcast.teded;

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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class EpisodeListCapture {
	private static String dirPath = "E:/langeasy/lucene/podcast/ted-ed/";

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		System.out.println("start time is : " + new Date());
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "8580");

		EpisodeListCapture downloader = new EpisodeListCapture();
		int count = 53;
		int start = 1;
		// start = 61;

		if (start > -1) {
			// archive(1);
			// System.out.println(new JSONArray(episodeLst).toString(3));
			// return;
		}

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
		for (int i = 0; i < 200; i++) {
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
			System.out.println(new JSONArray(jobStatus));
			if (allFinished) {
				File sFile = new File(dirPath + "episode-list.json");
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
				archive(jobIndex);
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

	public static void archive(int page) throws FileNotFoundException, IOException, ParseException {
		String url = "https://teded.herokuapp.com/lessons?_=1478743321877&page=" + page + "&subtitles=en+en-GB";
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url, 500);

		// File htmlFile = new File(dirPath + "archive1.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Elements liArr = doc.select(".lesson");

		for (Element li : liArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Elements taxonomyEles = li.select(".videoTaxonomy a");
				if (taxonomyEles.size() > 0) {
					Element taxonomyEle = taxonomyEles.get(0);
					String tlink = taxonomyEle.attr("href");
					String taxonomy = taxonomyEle.text();

					map.put("taxonomy", taxonomy);
					map.put("tlink", tlink);
				}

				Element textEle = li.select(".video-text a").get(0);
				String link = textEle.attr("href");
				String name = textEle.text();
				String duration = textEle.select(".lesson-title-duration").get(0).text();

				map.put("name", name);
				map.put("link", link);
				map.put("duration", duration);

				episodeLst.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(li);
				System.exit(0);
			}
		}
	}
}
