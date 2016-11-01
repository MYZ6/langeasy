package com.chenyi.langeasy.capture.podcast.freshair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class EpisodeListCapture {
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		// archive(null, null);
		archive("2010-09-28", "130187205");
	}

	static List<Map<String, Object>> episodeLst = new ArrayList<>();

	public static void archive(String lastEdate, String lastEid) throws FileNotFoundException, IOException,
			ParseException {
		String url = "http://www.npr.org/programs/fresh-air/archive";
		if (lastEdate != null) {
			url += "?date=" + lastEdate + "&eid=" + lastEid;
		}
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url);

		// File htmlFile = new
		// File("E:/langeasy/lucene/podcast/freshair/archive.html");
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

			String title = li.select("h1").get(0).text();

			Map<String, Object> map = new HashMap<>();
			map.put("eid", eid);
			map.put("edate", edate);
			map.put("title", title);

			Elements storyArr = li.select(".program-archive-segment");
			List<Map<String, String>> storyLst = new ArrayList<>();

			int count = 0;
			for (Element storyEle : storyArr) {
				Element typeEle = storyEle.select("h2").get(0);
				String type = typeEle.text();
				Element titleEle = storyEle.select("h1").get(0);
				String stitle = titleEle.text();

				Elements downloadBtn = storyEle.select(".audio-tool-download a");
				if (downloadBtn.size() == 0) {
					System.out.println(count + " has no download");
					continue;
				}
				String data = downloadBtn.get(0).attr("data-metrics");
				JSONObject json = new JSONObject(data);
				String storyId = json.getString("label");

				Map<String, String> story = new HashMap<>();
				story.put("sid", storyId);
				story.put("type", type);
				story.put("title", stitle);
				storyLst.add(story);
			}
			map.put("storyLst", storyLst);

			episodeLst.add(map);
			lastEdate = edate;
			lastEid = eid;
		}
		Date ldate = DateUtils.parseDate(lastEdate, new String[] { "yyyy-MM-dd" });
		String fdate = "2007-04-15";
		// fdate = "2016-10-15";
		Date finalDate = DateUtils.parseDate(fdate, new String[] { "yyyy-MM-dd" });
		if (ldate.compareTo(finalDate) < 0) {
			System.out.println(new JSONArray(episodeLst).toString(3));
			System.out.println("no more");
		} else {
			System.out.println(new JSONArray(episodeLst).toString());
			archive(lastEdate, lastEid);
		}
	}
}
