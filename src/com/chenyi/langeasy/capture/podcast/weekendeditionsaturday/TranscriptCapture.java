package com.chenyi.langeasy.capture.podcast.weekendeditionsaturday;

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
	private static int yearCount;
	private static int startYear;
	static List<JSONObject> downloadLst = new ArrayList<>();

	private static String dirPath = "E:/langeasy/lucene/podcast/weekend-edition-saturday/";

	private static JSONObject checkRepeat(String storyId) {
		for (JSONObject story : downloadLst) {
			if (story.getString("sid").equals(storyId)) {
				int count = 1;
				if (story.has("count")) {
					count = story.getInt("count");
				}
				count += 1;
				story.put("count", count);
				return story;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {

		startYear = 2004;
		yearCount = 12;
		// yearCount = -1;
		for (int i = yearCount; i >= 0; i--) {
			int year = startYear + i;

			File sFile = new File(dirPath + "episode-list" + year + ".json");
			String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

			JSONArray episodeList = new JSONArray(sResult);

			for (int j = 0; j < episodeList.length(); j++) {
				JSONObject episode = episodeList.getJSONObject(j);
				if (episode.has("storyLst")) {
					JSONArray storyLst = episode.getJSONArray("storyLst");
					for (int k = 0; k < storyLst.length(); k++) {
						JSONObject story = storyLst.getJSONObject(k);
						String sid = story.getString("sid");
						// JSONObject oldStory = checkRepeat(sid);
						// if (oldStory == null) {
						downloadLst.add(story);
						// } else {
						// System.out.println(oldStory);
						// }
					}
				}
			}
		}

		int total = downloadLst.size();
		System.out.println(total);

		int count = 100;
		step = total / count;
		if (total % step != 0) {
			count += 1;
		}
		// count = 110;
		System.out.println(step + "\t" + count);
		if (total > -1) {
			return;
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

				if (count % 5 == 4) {
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					parseStory(story.getString("sid"));
					Thread.sleep(3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 10) {
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

	private static void parseStory(String storyId) throws Exception {
		File saveFile = new File(dirPath + File.separator + "transcript" + File.separator + storyId + ".html");
		if (saveFile.exists()) {
			return;
		}

		String url = "http://www.npr.org/templates/transcript/transcript.php?storyId=" + storyId;

		Document doc = CaptureUtil.timeoutRequest(url);

		if (doc == null) {
			return;
		}
		Element transcriptEle = doc.select(".storytext").get(0);
		FileUtils.writeStringToFile(saveFile, transcriptEle.outerHtml(), StandardCharsets.UTF_8);
	}
}
