package com.chenyi.langeasy.capture.youtube.cnnbreakingnews;

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

import com.chenyi.langeasy.capture.CaptureUtil;
import com.chenyi.langeasy.capture.podcast.yalecourses.MatcherUtil;
import com.chenyi.langeasy.capture.youtube.ParseUtil;

public class CaptionFetch {
	static JSONArray videoList;
	static List<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;

	static List<String> nocaptionLst = new ArrayList<>();

	private static String dirPath = "E:/langeasy/lucene/youtube/cnn-breaking-news/";

	private static JSONObject checkRepeat(String videoId) {
		for (JSONObject video : downloadLst) {
			if (video.getString("vid").equals(videoId)) {
				int count = 1;
				if (video.has("count")) {
					count = video.getInt("count");
				}
				count += 1;
				video.put("count", count);
				return video;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("start time is : " + new Date());

		File sFile = new File(dirPath + "video-list.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		videoList = new JSONArray(sResult);
		for (int i = 0; i < videoList.length(); i++) {
			JSONObject video = videoList.getJSONObject(i);
			String link = video.getString("link");
			String vid = MatcherUtil.getVid(link);

			String saveFilePath = dirPath + "caption/" + vid + ".xml";
			File saveFile = new File(saveFilePath);
			if (saveFile.exists()) {
				continue;
			}
			if (video.has("nocaption")) {
				if (!"hauoepPqns4".equals(vid)) {
					// continue;
				}
			}
			video.put("vid", vid);
			video.put("index", i);
			// JSONObject oldLecture = checkRepeat(vid);
			// if (oldLecture == null) {
			downloadLst.add(video);
			// } else {
			// System.out.println(oldLecture);
			// }
		}

		int total = downloadLst.size();
		System.out.println(total);
		int count = 150;
		// step = 17;
		step = total / count;
		if (total % step != 0) {
			step += 1;
			count = total / step + 1;
		}
		// count = 152;
		System.out.println(step + "\t" + count);
		if (total > -1) {
			// return;
		}

		jobStatus = new int[count];
		int start = 0;
		int end = count;
		// end = 3;
		CaptionFetch downloader = new CaptionFetch();
		for (int i = start; i < end; i++) {
			Job job = downloader.new Job(i);
			jobStatus[i] = 0;
			job.start();
			if (i > 0) {
				// break;
			}
		}
		for (int i = 0; i < 200; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			boolean allFinished = true;
			for (int j = start; j < end; j++) {
				if (jobStatus[j] == 0) {
					allFinished = false;
					break;
				}
			}
			System.out.println(new JSONArray(jobStatus));
			System.out.println(new JSONArray(nocaptionLst));
			if (allFinished) {
				System.out.println(new JSONArray(nocaptionLst).toString(3));
				System.out.println("nocaptionLst size:" + nocaptionLst.size());
				if (nocaptionLst.size() > 0) {
					// FileUtils.writeStringToFile(sFile, videoList.toString(3), StandardCharsets.UTF_8);
				}
				break;
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
			for (JSONObject video : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					String vid = video.getString("vid");
					int result = fetch(vid);
					if (result == 1) {
						setCaptionStatus(video);
					}
					Thread.sleep(300);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count > 5) {
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

		private void setCaptionStatus(JSONObject video) {
			int index = video.getInt("index");
			JSONObject oriVideo = videoList.getJSONObject(index);
			oriVideo.put("nocaption", 1);
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}

	private static int fetch(String videoid) throws Exception {
		String saveFilePath = dirPath + "caption/" + videoid + ".xml";
		File saveFile = new File(saveFilePath);

		String url = "https://www.youtube.com/watch?v=" + videoid;
		Document doc = CaptureUtil.timeoutRequest(url);
		if (doc == null) {
			return 0;
		}
		String content = doc.body().html();
		String ttsUrl = MatcherUtil.getTurl(content);
		if (ttsUrl == null) {
			nocaptionLst.add(videoid);
			return 1;
		}

		Document tdoc = null;
		try {
			tdoc = CaptureUtil.timeoutRequest(ttsUrl);
		} catch (IllegalArgumentException e) {
			System.out.println("Malformed URL: " + ttsUrl);
			System.out.println("Malformed URL vid: " + videoid);
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		if (tdoc == null) {
			// nocaptionLst.add(videoid);
			return 0;
		}
		ParseUtil.shrink(tdoc);
		FileUtils.writeStringToFile(saveFile, tdoc.outerHtml(), StandardCharsets.UTF_8);
		return 2;
	}
}
