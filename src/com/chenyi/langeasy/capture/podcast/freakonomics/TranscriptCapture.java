package com.chenyi.langeasy.capture.podcast.freakonomics;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.chenyi.langeasy.capture.CaptureUtil;

public class TranscriptCapture {
	static List<JSONObject> downloadLst = new ArrayList<>();

	private static String dirPath = "E:/langeasy/lucene/podcast/freakonomics/";

	public static void main(String[] args) throws Exception {
		System.out.println("start time is : " + new Date());
		System.setProperty("http.proxyHost", "127.0.0.1");
		System.setProperty("http.proxyPort", "8580");

		File sFile = new File(dirPath + "episode-list.json");
		// sFile = new File(dirPath + "test.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		JSONArray episodeList = new JSONArray(sResult);
		for (int i = 0; i < episodeList.length(); i++) {
			JSONObject episode = episodeList.getJSONObject(i);
			downloadLst.add(episode);
		}

		int total = downloadLst.size();
		System.out.println(total);
		int count = 10;// little sites using less connections
		step = total / count;
		if (total % step != 0) {
			count += 1;
		}
		System.out.println(step + "\t" + count);
		if (total > -1) {
			// parseStory("http://freakonomics.com/2010/02/06/the-dangers-of-safety-full-transcript/");
			// return;
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
				if (count % 20 == 19) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					parseStory(story.getString("link"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 1) {
					// break;
				}
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

	private static void parseStory(String link) throws Exception {
		String filename = link.substring(35, link.lastIndexOf("/"));
		File saveFile = new File(dirPath + File.separator + "transcript" + File.separator + filename + ".html");
		if (saveFile.exists()) {
			return;
		}

		System.out.println(link);
		Document doc = CaptureUtil.timeoutRequest(link);

		// File htmlFile = new File(dirPath + "transcript.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Element transcriptEle = doc.select(".category-podcast-transcripts").get(0);
		FileUtils.writeStringToFile(saveFile, transcriptEle.outerHtml(), StandardCharsets.UTF_8);
	}
}
