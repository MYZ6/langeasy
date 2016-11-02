package com.chenyi.langeasy.capture.podcast.atc;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class StoryListCapture {

	private static String dirPath = "E:/langeasy/lucene/podcast/allthingsconsidered/";
	private static int yearCount;
	private static int startYear;
	static Map<Integer, JSONArray> episodeYearMap = new HashMap<>();
	private static ArrayList<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		System.out.println("start time is : " + new Date());

		// List<Map<String, String>> storyLst = parse(null);
		// System.out.println(new JSONArray(storyLst).toString(3));
		// int a = 3;
		// if (a > 0) {
		// return;
		// }
		startYear = 2005;
		yearCount = 11;
		for (int i = yearCount; i >= 0; i--) {
			int year = startYear + i;

			File sFile = new File(dirPath + "episode-list" + year + ".json");
			String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

			JSONArray episodeList = new JSONArray(sResult);
			episodeYearMap.put(year, episodeList);

			for (int j = 0; j < episodeList.length(); j++) {
				JSONObject episode = episodeList.getJSONObject(j);
				if (episode.has("storyLst")) {
					continue;
				}
				episode.put("year", year);// easy for matching later
				episode.put("index", j);// easy for matching later
				downloadLst.add(episode);
			}
		}

		// System.out.println(episodeList);

		int total = downloadLst.size();
		System.out.println(total);
		step = 500;
		// int count = total / step;
		int count = 100;
		step = total / count;
		if (step == 0) {
			count = total % count;
			step = total / count;
			System.out.println(step + "\t" + count);
		}
		if (total > -1) {
			// return;
		}
		// if (total % count != 0) {
		// count += 1;
		// }
		jobStatus = new int[count];
		int start = 0;
		StoryListCapture downloader = new StoryListCapture();
		for (int i = start; i < count; i++) {
			Job job = downloader.new Job(i);
			jobStatus[i] = 0;
			job.start();
			if (i > 2) {
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
				for (int k = yearCount; k >= 0; k--) {
					int year = startYear + k;

					File sFile = new File(dirPath + "episode-list" + year + ".json");
					FileUtils.writeStringToFile(sFile, episodeYearMap.get(year).toString(3), StandardCharsets.UTF_8);
				}
				break;
			}
		}

	}

	private static void updateEpisode(int year, int index, List<Map<String, String>> storyLst) {
		JSONObject episode = episodeYearMap.get(year).getJSONObject(index);
		episode.put("storyLst", storyLst);
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
			int start = jobIndex * step;
			int end = start + step;
			if (end > downloadLst.size()) {
				end = downloadLst.size();
			}
			System.out.println(start + "\t" + end);

			List<JSONObject> subLst = downloadLst.subList(start, end);
			int count = 0;
			for (JSONObject episode : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					List<Map<String, String>> storyLst = parse(episode.getString("eid"));
					updateEpisode(episode.getInt("year"), episode.getInt("index"), storyLst);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 28) {
					break;
				}
			}

			jobStatus[jobIndex] = 1;
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

	public static List<Map<String, String>> parse(String eid) throws FileNotFoundException, IOException, ParseException {
		String url = "http://www.npr.org/programs/all-things-considered/" + eid;
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url);

		// File htmlFile = new File(dirPath + "episode.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return null;
		}
		doc.getElementById("storylist");
		Elements liArr = doc.select("#storylist .story");

		List<Map<String, String>> storyLst = new ArrayList<>();

		int count = 0;
		for (Element li : liArr) {
			count++;
			Elements slugElements = li.select(".storyinfo h2.slug");
			String slug = "";
			if (slugElements.size() > 0) {
				slug = slugElements.get(0).text();
			}
			String storyTitle = li.select(".storyinfo h1 a").get(0).text();

			Elements downloadBtn = li.select(".audio-tool-download a");
			if (downloadBtn.size() == 0) {
				System.out.println(count + "/" + storyTitle + " has no download");
				continue;
			}
			Elements transcriptBtn = li.select(".audio-tool-transcript a");
			if (transcriptBtn.size() == 0) {
				System.out.println(count + "/" + storyTitle + " has no transcript");
				continue;
			}
			String data = downloadBtn.get(0).attr("data-metrics");
			JSONObject json = new JSONObject(data);
			String storyId = json.getString("label");

			Map<String, String> map = new HashMap<>();
			map.put("sid", storyId);
			map.put("slug", slug);
			map.put("title", storyTitle);
			storyLst.add(map);
		}
		return storyLst;
	}
}
