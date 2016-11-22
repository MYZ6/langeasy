package com.chenyi.langeasy.capture.podcast.yalecourses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import com.chenyi.langeasy.capture.CaptureUtil;

public class LectureListCapture {
	private static String dirPath = "E:/langeasy/lucene/podcast/yale-courses/";
	private static JSONArray courseList;
	private static ArrayList<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

		File sFile = new File(dirPath + "course-list.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");
		courseList = new JSONArray(sResult);

		for (int i = 0; i < courseList.length(); i++) {
			JSONObject course = courseList.getJSONObject(i);
			if (course.has("lectureLst")) {
				continue;
			}
			course.put("index", i);// easy for matching later
			downloadLst.add(course);
		}

		int total = downloadLst.size();
		System.out.println(total);
		int count = 100;
		step = total / count;
		step += 1;
		count = total / step;
		System.out.println(step + "\t" + count);
		if (total > -1) {
			// return;
		}

		jobStatus = new int[count];
		int start = 0;
		int end = count;
		LectureListCapture downloader = new LectureListCapture();
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
			if (allFinished) {
				FileUtils.writeStringToFile(sFile, courseList.toString(3), StandardCharsets.UTF_8);
				break;
			}
		}

		// List<Map<String, String>> lectureLst = parse(null);
		// System.out.println(new JSONArray(lectureLst));
	}

	private static void updateCourse(int index, List<Map<String, String>> lectureLst) {
		JSONObject course = courseList.getJSONObject(index);
		course.put("lectureLst", lectureLst);
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
			for (JSONObject course : subLst) {
				count++;
				System.err.println("job" + jobIndex + " download seq : " + count);
				try {
					String url = "https://www.youtube.com" + course.getString("link");
					System.out.println(url);
					Document doc = CaptureUtil.timeoutRequest(url);

					if (doc == null) {
						break;
					}
					List<Map<String, String>> lectureLst = PlaylistPageParser.parse(doc);
					updateCourse(course.getInt("index"), lectureLst);
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

	public static List<Map<String, String>> parse(String link) throws FileNotFoundException, IOException,
			ParseException {
		String url = "https://www.youtube.com" + link;
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url);

		// File htmlFile = new File(dirPath + "course.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return null;
		}
		List<Map<String, String>> lectureLst = PlaylistPageParser.parse(doc);
		return lectureLst;
	}
}
