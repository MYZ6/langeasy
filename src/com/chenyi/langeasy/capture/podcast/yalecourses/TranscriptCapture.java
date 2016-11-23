package com.chenyi.langeasy.capture.podcast.yalecourses;

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

public class TranscriptCapture {
	static List<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;

	static List<String> notranscriptLst = new ArrayList<>();

	private static String dirPath = "E:/langeasy/lucene/podcast/yale-courses/";

	private static JSONObject checkRepeat(String videoId) {
		for (JSONObject lecture : downloadLst) {
			if (lecture.getString("vid").equals(videoId)) {
				int count = 1;
				if (lecture.has("count")) {
					count = lecture.getInt("count");
				}
				count += 1;
				lecture.put("count", count);
				return lecture;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.out.println("start time is : " + new Date());

		File sFile = new File(dirPath + "course-list.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		JSONArray courseList = new JSONArray(sResult);
		for (int i = 0; i < courseList.length(); i++) {
			JSONObject course = courseList.getJSONObject(i);
			if (course.has("lectureLst")) {
				JSONArray lectureLst = course.getJSONArray("lectureLst");
				for (int j = 0; j < lectureLst.length(); j++) {
					JSONObject lecture = lectureLst.getJSONObject(j);
					String link = lecture.getString("link");
					String vid = MatcherUtil.getVid(link);

					String saveFilePath = dirPath + "transcript/" + vid + ".xml";
					File saveFile = new File(saveFilePath);
					if (saveFile.exists()) {
						continue;
					}
					lecture.put("vid", vid);
					// JSONObject oldLecture = checkRepeat(vid);
					// if (oldLecture == null) {
					downloadLst.add(lecture);
					// } else {
					// System.out.println(oldLecture);
					// }
				}
			}
		}

		int total = downloadLst.size();
		System.out.println(total);
		int count = 89;
		step = total / count;
		if (total % step != 0) {
			count += 1;
		}
		System.out.println(step + "\t" + count);
		if (total > -1) {
			return;
		}

		jobStatus = new int[count];
		int start = 0;
		int end = count;
		TranscriptCapture downloader = new TranscriptCapture();
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
			System.out.println(new JSONArray(notranscriptLst));
			if (allFinished) {
				System.out.println(new JSONArray(notranscriptLst).toString(3));
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
			for (JSONObject lecture : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					String vid = lecture.getString("vid");
					fetch(vid);
					Thread.sleep(300);
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

	private static void fetch(String videoid) throws Exception {
		String saveFilePath = dirPath + "transcript/" + videoid + ".xml";
		File saveFile = new File(saveFilePath);

		String url = "https://www.youtube.com/watch?v=" + videoid;
		Document doc = CaptureUtil.timeoutRequest(url);
		if (doc == null) {
			return;
		}
		String content = doc.body().html();
		String ttsUrl = MatcherUtil.getTurl(content);
		if (ttsUrl == null) {
			notranscriptLst.add(videoid);
			return;
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
			return;
		}
		FileUtils.writeStringToFile(saveFile, tdoc.outerHtml(), StandardCharsets.UTF_8);
	}
}
