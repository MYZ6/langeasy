package com.chenyi.langeasy.capture.podcast.atc;

import java.io.File;
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
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class EpisodeListCapture {
	private static List<String> dateList;
	private static int yearCount;

	private static String dirPath = "E:/langeasy/lucene/podcast/allthingsconsidered/";
	private static int startYear;
	private static int periodCount;

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		startYear = 2006;
		yearCount = 10;
		periodCount = 6;

		dateList = new ArrayList<>();
		for (int i = yearCount; i >= 0; i--) {
			int year = startYear + i;
			dateList.add(year + "-12-31");
			dateList.add(year + "-10-31");
			dateList.add(year + "-08-31");
			dateList.add(year + "-06-30");
			dateList.add(year + "-04-30");
			dateList.add(year + "-02-28");
			List<Map<String, Object>> episodeLst = new ArrayList<>();
			episodeYearMap.put(year, episodeLst);
		}
		dateList.add((startYear - 1) + "-12-31");
		// for (int i = 0; i < dateList.size() - 1; i++) {
		// String maxDate = dateList.get(i);
		// String minDate = dateList.get(i + 1);
		// System.out.println(maxDate + "\t" + minDate);
		// }
		System.out.println(dateList);

		// int testYear = 2010;
		// archive(testYear, "2010-09-28", "130187205", "2010-08-28");
		// System.out.println(new
		// JSONArray(episodeYearMap.get(testYear)).toString(3));
		if (yearCount > -1) {
			// return;
		}

		System.out.println("start time is : " + new Date());

		EpisodeListCapture downloader = new EpisodeListCapture();
		int count = dateList.size() - 1;
		int start = 0;
		jobStatus = new int[count];
		for (int i = start; i < count; i++) {
			if (i > -1) {
				// return;
			}
			jobStatus[i] = 0;
			Job job = downloader.new Job(i);
			job.start();
			if (i % 10 == 9) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// return;
			}
		}
		for (int i = 0; i < 200; i++) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// boolean[] yearFinished = new boolean[yearCount];
			Map<Integer, Boolean> yearFinished = new HashMap<>();
			boolean allFinished = true;
			for (int j = 0; j <= yearCount; j++) {
				boolean finished = true;
				int index = (yearCount - j) * periodCount;// reverse sequence
				for (int k = 0; k < periodCount; k++) {
					if (jobStatus[index + k] == 0) {
						finished = false;
						allFinished = false;
					}
				}
				int year = startYear + j;
				yearFinished.put(year, finished);
			}
			for (int j = yearCount; j >= 0; j--) {
				int year = startYear + j;
				if (yearFinished.get(year)) {
					File sFile = new File(dirPath + "episode-list" + year + ".json");
					FileUtils.writeStringToFile(sFile, new JSONArray(episodeYearMap.get(year)).toString(3),
							StandardCharsets.UTF_8);
					System.out.println("end time is : " + new Date());
				}
			}
			if (allFinished) {
				System.out.println("end time is : " + new Date());
				break;
			}
		}
	}

	static Map<Integer, List<Map<String, Object>>> episodeYearMap = new HashMap<>();

	public static void archive(int year, String lastEdate, String lastEid, String minDate)
			throws FileNotFoundException, IOException, ParseException {
		String url = "http://www.npr.org/programs/all-things-considered/archive";
		if (lastEdate != null) {
			url += "?date=" + lastEdate + "&eid=" + lastEid;
		}
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url);

		// File htmlFile = new File(dirPath + "archive.html");
		// String sResult = IOUtils.toString(new FileInputStream(htmlFile),
		// "utf-8");
		// Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Elements liArr = doc.select(".program-archive-episode");

		List<Map<String, Object>> episodeLst = episodeYearMap.get(year);
		for (Element li : liArr) {
			String eid = li.attr("data-episode-id");
			String edate = li.attr("data-episode-date");

			String title = li.select("h1.date").get(0).text();

			Map<String, Object> map = new HashMap<>();
			map.put("eid", eid);
			map.put("edate", edate);
			map.put("title", title);

			episodeLst.add(map);
			lastEdate = edate;
			lastEid = eid;
		}
		Date ldate = DateUtils.parseDate(lastEdate, new String[] { "yyyy-MM-dd" });
		// String fdate = "startYear-04-15";
		// fdate = "2016-10-15";
		Date finalDate = DateUtils.parseDate(minDate, new String[] { "yyyy-MM-dd" });
		if (ldate.compareTo(finalDate) < 0) {
			// System.out.println(new JSONArray(episodeLst).toString(3));
			System.out.println("no more");
		} else {
			System.out.println(lastEdate);
			// System.out.println(new JSONArray(episodeLst).toString());
			archive(year, lastEdate, lastEid, minDate);
		}
	}

	private static int[] jobStatus;

	class Job implements Runnable {
		private Thread t;
		private int jobIndex;

		Job(int jobIndex) {
			this.jobIndex = jobIndex;
			System.out.println("Creating job " + jobIndex);
		}

		public void run() {
			int year = startYear + (yearCount - jobIndex / periodCount);
			try {
				String maxDate = dateList.get(jobIndex);
				String minDate = dateList.get(jobIndex + 1);
				System.out.println(maxDate + "\t" + minDate);
				archive(year, maxDate, null, minDate);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
			jobStatus[jobIndex] = 1;
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
