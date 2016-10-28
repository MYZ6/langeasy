package com.chenyi.langeasy.capture.podcast.tedradiohour;

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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.chenyi.langeasy.capture.CaptureUtil;

public class EpisodeListCapture {
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		archive(null, null);
		// archive("481290551", "2016-06-10");
	}

	static List<Map<String, String>> episodeLst = new ArrayList<>();

	public static void archive(String lastEid, String lastEdate) throws FileNotFoundException, IOException,
			ParseException {
		String url = "http://www.npr.org/programs/ted-radio-hour/archive";
		if (lastEdate != null) {
			url += "?date=" + lastEdate + "&eid=" + lastEid;
		}
		System.out.println(url);
		Document doc = CaptureUtil.timeoutRequest(url);

		// File htmlFile = new File("E:/langeasy/ted-radio-hour.html");
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

			String title = li.select("h2.title").get(0).text();

			Map<String, String> map = new HashMap<>();
			map.put("eid", eid);
			map.put("edate", edate);
			map.put("title", title);
			episodeLst.add(map);
			lastEid = eid;
			lastEdate = edate;
		}
		Date ldate = DateUtils.parseDate(lastEdate, new String[] { "yyyy-MM-dd" });
		Date finalDate = DateUtils.parseDate("2012-04-15", new String[] { "yyyy-MM-dd" });
		System.out.println(new JSONArray(episodeLst));
		if (ldate.compareTo(finalDate) < 0) {
			System.out.println("no more");
		} else {
			archive(lastEid, lastEdate);
		}
	}
}
