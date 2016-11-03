package com.chenyi.langeasy.capture.podcast.morningedition;

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

	private static String dirPath = "E:/langeasy/lucene/podcast/morningedition/";

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		// archive("2010-09-28", "130187205", "2010-08-28");
		// System.out.println(new JSONArray(episodeLst).toString(3));

		int yearCount = 11;
		dateList = new ArrayList<>();
		for (int i = yearCount; i >= 0; i--) {
			int year = 2005 + i;
			dateList.add(year + "-12-31");
			dateList.add(year + "-08-31");
			dateList.add(year + "-04-30");
		}
		// for (int i = 0; i < dateList.size() - 1; i++) {
		// String maxDate = dateList.get(i);
		// String minDate = dateList.get(i + 1);
		// System.out.println(maxDate + "\t" + minDate);
		// }
		System.out.println(dateList);
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
			boolean allFinished = true;
			for (int j = start; j < count; j++) {
				if (jobStatus[j] == 0) {
					allFinished = false;
					break;
				}
			}
			if (allFinished) {
				File sFile = new File(dirPath + "episode-list.json");
				FileUtils.writeStringToFile(sFile, new JSONArray(episodeLst).toString(3), StandardCharsets.UTF_8);
				System.out.println("end time is : " + new Date());
				break;
			}
		}
	}

	static List<Map<String, Object>> episodeLst = new ArrayList<>();

	public static void archive(String lastEdate, String lastEid, String minDate) throws FileNotFoundException,
			IOException, ParseException {
		String url = "http://www.npr.org/programs/morning-edition/archive";
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
		// String fdate = "2005-04-15";
		// fdate = "2016-10-15";
		Date finalDate = DateUtils.parseDate(minDate, new String[] { "yyyy-MM-dd" });
		if (ldate.compareTo(finalDate) < 0) {
			// System.out.println(new JSONArray(episodeLst).toString(3));
			System.out.println("no more");
		} else {
			System.out.println(lastEdate);
			// System.out.println(new JSONArray(episodeLst).toString());
			archive(lastEdate, lastEid, minDate);
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
			try {
				String maxDate = dateList.get(jobIndex);
				String minDate = dateList.get(jobIndex + 1);
				System.out.println(maxDate + "\t" + minDate);
				archive(maxDate, null, minDate);
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
