package com.chenyi.langeasy.capture.podcast.yalecourses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VideosPageParser {
	static List<Map<String, String>> episodeLst = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// File file = new File("e:/browse_ajax");
		File file = new File("e:/langeasy/lucene/podcast/yale-courses/browse_ajax_p");
		String sjson = IOUtils.toString(new FileInputStream(file), "utf-8");
		String content = new JSONObject(sjson).getString("content_html");
		Document doc = Jsoup.parse(content);

		if (doc == null) {
			return;
		}
		Elements eleArr = doc.select(".yt-lockup-title");

		for (Element ele : eleArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Element hele = ele.select("a").get(0);
				String link = hele.attr("href");
				String name = hele.text();
				String duration = ele.select("span").get(0).text();

				map.put("name", name);
				map.put("link", link);
				map.put("duration", duration);

				episodeLst.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(ele);
				System.exit(0);
			}
		}
		System.out.println(new JSONArray(episodeLst).toString(3));
	}
}
