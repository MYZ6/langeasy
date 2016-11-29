package com.chenyi.langeasy.capture.youtube.tedxtalks;

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
import com.chenyi.langeasy.capture.youtube.ParseUtil;

public class VideoCapture {
	private static String dirPath;
	private static JSONArray collectionList;
	private static ArrayList<JSONObject> downloadLst = new ArrayList<>();
	private static int[] jobStatus;
	private static int subCollectionIndex;

	public static void handle(int sindex, String subPath) throws FileNotFoundException, IOException, ParseException {
		dirPath = subPath;
		subCollectionIndex = sindex;
		File sFile = new File(dirPath + "playlists.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");
		collectionList = new JSONArray(sResult);

		for (int i = 0; i < collectionList.length(); i++) {
			JSONObject playlist = collectionList.getJSONObject(i);
			if (playlist.has("videoLst")) {
				continue;
			}
			playlist.put("index", i);// easy for matching later
			downloadLst.add(playlist);
		}

		int total = downloadLst.size();
		System.out.println(total);
		if (total == 0) {
			// return;
		}
		int count = 150;
		// step = 17;
		step = total / count;
		if (step == 0) {
			step = 1;
			count = total / step;
		} else if (total % step != 0) {
			step += 1;
			count = total / step + 1;
		}
		System.out.println(step + "\t" + count);
		if (total > -1) {
			return;
		}

		jobStatus = new int[count];
		int start = 0;
		int end = count;
		VideoCapture downloader = new VideoCapture();
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
				FileUtils.writeStringToFile(sFile, collectionList.toString(3), StandardCharsets.UTF_8);
				break;
			}
		}

		// List<Map<String, String>> videoLst = parse(null);
		// System.out.println(new JSONArray(videoLst));
	}

	private static void updateCollection(int index, List<Map<String, String>> videoLst) {
		JSONObject playlist = collectionList.getJSONObject(index);
		playlist.put("videoLst", videoLst);
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
			for (JSONObject playlist : subLst) {
				count++;
				System.err.println("subCollectionIndex " + subCollectionIndex + "/job" + jobIndex + " download seq : "
						+ count);
				try {
					String url = "https://www.youtube.com" + playlist.getString("link");
					System.out.println(url);
					Document doc = CaptureUtil.timeoutRequest(url);

					if (doc == null) {
						break;
					}
					List<Map<String, String>> videoLst = ParseUtil.playlist(url);
					updateCollection(playlist.getInt("index"), videoLst);
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
			System.out.println("subCollectionIndex " + subCollectionIndex + "/job" + jobIndex + " last time is : "
					+ new Date());
		}

		public void start() {
			System.out.println("Starting job " + jobIndex);
			if (t == null) {
				t = new Thread(this, "job" + jobIndex);
				t.start();
			}
		}

	}
}
