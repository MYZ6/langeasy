package com.chenyi.langeasy.capture.podcast.teded;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.chenyi.langeasy.capture.CaptureUtil;

public class YoutubeLinkCapture {
	private static String dirPath = "E:/langeasy/lucene/podcast/ted-ed/";
	private static JSONArray episodeList;
	private static ArrayList<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;

	public static void main(String[] args) throws Exception {

		File sFile = new File(dirPath + "episode-list.json");
		// sFile = new File(dirPath + "test.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		// String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		episodeList = new JSONArray(sResult);
		// System.out.println(episodeList);

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
		int count = 100;
		step = total / count;
		if (total % step != 0) {
			count += 3;
		}
		// step = 3;
		System.out.println(step + "\t" + count);
		if (total > -1) {
			// String link =
			// "/lessons/why-is-the-us-constitution-so-hard-to-amend-peter-paccone";
			// JSONObject testEpisode = new JSONObject();
			// testEpisode.put("link", link);
			// parse(testEpisode);
			// System.out.println(testEpisode.toString(3));
			// return;
		}

		jobStatus = new int[count];
		int start = 0;
		YoutubeLinkCapture downloader = new YoutubeLinkCapture();
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

	private static void updateEpisode(int index, String lessonid, String videoid) {
		JSONObject episode = episodeList.getJSONObject(index);
		episode.put("lessonid", lessonid);
		episode.put("videoid", videoid);
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
					parse(episode);
					updateEpisode(episode.getInt("index"), episode.getString("lessonid"), episode.getString("videoid"));
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

	private static void parse(JSONObject episode) throws Exception {
		String link = episode.getString("link");
		String url = "https://teded.herokuapp.com" + link;
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url, 500);

		// File htmlFile = new File(dirPath + "lesson.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Element playerContainer = doc.getElementById("playerContainer");
		String lessonid = playerContainer.attr("data-lesson-id");
		System.out.println("lessonid: " + lessonid);
		episode.put("lessonid", lessonid);
		episode.put("videoid", playerContainer.attr("data-video-id"));
	}

}
