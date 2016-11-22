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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PlaylistsPageParser {
	static List<Map<String, String>> courseLst = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		// File file = new File("e:/browse_ajax");
		// File file = new
		// File("e:/langeasy/lucene/podcast/yale-courses/browse_ajax_p");
		File file = new File("e:/langeasy/lucene/podcast/yale-courses/playlists.html");
		String content = IOUtils.toString(new FileInputStream(file), "utf-8");
		// String content = new JSONObject(sjson).getString("content_html");
		// System.out.println(content);
		int total = 1;
		if (total > 0) {
			// return;
		}
		Document doc = Jsoup.parse(content);

		if (doc == null) {
			return;
		}
		Elements eleArr = doc.select(".channels-content-item");

		for (Element ele : eleArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Element hele = ele.select(".yt-uix-tile-link").get(0);
				String link = hele.attr("href");
				String name = hele.text();
				String count = ele.select(".formatted-video-count-label b").get(0).text();

				map.put("name", name);
				map.put("link", link);
				map.put("count", count);

				courseLst.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(ele);
				System.exit(0);
			}
		}
		System.out.println(new JSONArray(courseLst).toString(3));
	}
}
