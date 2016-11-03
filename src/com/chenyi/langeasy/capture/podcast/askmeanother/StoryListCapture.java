package com.chenyi.langeasy.capture.podcast.askmeanother;

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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class StoryListCapture {
	private static String dirPath = "E:/langeasy/lucene/podcast/ask-me-another/";
	private static JSONArray episodeList;
	private static ArrayList<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

		File sFile = new File(dirPath + "episode-list.json");
		// sFile = new File(dirPath + "test.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		// String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		episodeList = new JSONArray(sResult);
		System.out.println(episodeList);

		for (int i = 0; i < episodeList.length(); i++) {
			JSONObject episode = episodeList.getJSONObject(i);
			if (episode.has("storyLst")) {
				continue;
			}
			episode.put("index", i);// easy for matching later
			downloadLst.add(episode);
		}

		int total = downloadLst.size();
		System.out.println(total);
		int count = 81;
		step = total / count;
		if (total % step != 0) {
			count += 1;
		}
		step = 3;
		System.out.println(step + "\t" + count);
		if (total > -1) {
			// return;
		}

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
				FileUtils.writeStringToFile(sFile, episodeList.toString(3), StandardCharsets.UTF_8);
				break;
			}
		}

		// List<Map<String, String>> storyLst = parse(null);
		// System.out.println(new JSONArray(storyLst));
	}

	private static void updateEpisode(int index, List<Map<String, String>> storyLst) {
		JSONObject episode = episodeList.getJSONObject(index);
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
			CloseableHttpClient httpclient = HttpClients.createDefault();
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
					updateEpisode(episode.getInt("index"), storyLst);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 12) {
					// break;
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
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
		String url = "http://www.npr.org/" + eid;
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
			Element infoEle = li.select(".storyinfo h1 a").get(0);
			String storyTitle = infoEle.text();

			Elements downloadBtn = li.select(".audio-tool-download a");
			if (downloadBtn.size() == 0) {
				System.out.println(count + " has no download");
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
			map.put("title", storyTitle);
			storyLst.add(map);
		}
		return storyLst;
	}
}
