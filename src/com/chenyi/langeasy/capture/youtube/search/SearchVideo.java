package com.chenyi.langeasy.capture.youtube.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

public class SearchVideo {
	static JSONObject savedList;
	static List<String> downloadLst;
	private static int[] jobStatus;

	private static String dirPath;
	private static int subCollectionIndex;

	public static void main(String[] args) throws Exception {
		dirPath = "E:/langeasy/lucene/youtube/searchcaption/";
		int type = 1;
		type = 2;
		if (type == 1) {
			handle();
		} else {
			CaptionFetch.handle(0, dirPath);
		}
	}

	public static void handle() throws Exception {
		// dirPath = subPath;
		// subCollectionIndex = sindex;
		downloadLst = new ArrayList<>();

		File sFile = new File(dirPath + "search-result.json");
		if (sFile.exists()) {
			String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");
			savedList = new JSONObject(sResult);
		} else {
			savedList = new JSONObject();
		}

		String[] searchArr = new String[] { "abaft", "approbatory", "betroth", "bungler", "churlishness",
				"contumacious", "deliquesce", "extricable", "froward", "imprecate", "incommodious", "ligneous",
				"minatory", "nefariousness", "objurgate", "obtrude", "opprobrious", "preponderate", "recusant",
				"retroaction", "sapid", "sedulous", "traduce", "tumid", "unwonted" };

		for (String word : searchArr) {
			if (savedList.has(word)) {
				continue;
			}
			downloadLst.add(word);
		}

		int total = downloadLst.size();
		System.out.println(total);
		if (total == 0) {
			return;
		}
		int count = total;
		step = 1;
		System.out.println(step + "\t" + count);
		if (total > -1) {
			// System.out.println(collectionList.toString(3));
			// return;
		}

		jobStatus = new int[count];
		int start = 0;
		int end = count;
		// end = 1;
		SearchVideo downloader = new SearchVideo();
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
			// Collections.synchronizedList(nocaptionLst);
			if (allFinished) {
				FileUtils.writeStringToFile(sFile, savedList.toString(), StandardCharsets.UTF_8);
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

			List<String> subLst = downloadLst.subList(start, end);
			int count = 0;
			for (String word : subLst) {
				count++;
				System.err.println("subCollectionIndex " + subCollectionIndex + "/job" + jobIndex + " download seq : "
						+ count);
				try {
					List<Map<String, String>> videoLst = search(word);
					savedList.put(word, videoLst);
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

	private static List<Map<String, String>> search(String word) throws Exception {
		String url = "https://www.youtube.com/results?search_query=" + word + "%2C+cc&spfreload=1";
		Document doc = CaptureUtil.timeoutRequest(url);

		// File htmlFile = new File(dirPath + "search-result.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile), "utf-8");
		// Document doc = Jsoup.parse(sResult);
		if (doc == null) {
			return null;
		}
		// System.out.println(doc.html());
		Elements eleArr = doc.select(".yt-lockup-video");

		List<Map<String, String>> videoLst = new ArrayList<>();
		for (Element ele : eleArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Element hele = ele.select(".yt-uix-tile-link").get(0);
				String link = hele.attr("href");
				String name = hele.text();

				Elements ownerEles = ele.select(".yt-lockup-byline a");
				if (ownerEles.size() == 0) {
					continue;
				}
				Element ownerEle = ownerEles.get(0);
				String olink = ownerEle.attr("href");
				String oname = ownerEle.text();

				String duration = ele.select(".video-time").get(0).text();

				map.put("name", name);
				map.put("link", link);
				map.put("oname", oname);
				map.put("olink", olink);
				map.put("duration", duration);

				videoLst.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(ele);
				System.exit(0);
			}
		}
		int total = videoLst.size();
		System.out.println(total);
		if (total == 0) {
			System.err.println(url);
		} else {
			System.out.println(videoLst.get(total - 1));
		}
		// System.out.println(new JSONArray(videoLst).toString(3));

		return videoLst;
	}
}
