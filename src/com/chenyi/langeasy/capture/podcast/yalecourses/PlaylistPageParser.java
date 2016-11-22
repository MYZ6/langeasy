package com.chenyi.langeasy.capture.podcast.yalecourses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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

public class PlaylistPageParser {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		File file = new File("e:/langeasy/lucene/podcast/yale-courses/playlist.html");
		String content = IOUtils.toString(new FileInputStream(file), "utf-8");
		int total = 1;
		if (total > 0) {
			// return;
		}
		Document doc = Jsoup.parse(content);

		if (doc == null) {
			return;
		}

		List<Map<String, String>> lectureLst = parse(doc);
		System.out.println(new JSONArray(lectureLst).toString(3));
	}

	public static List<Map<String, String>> parse(Document doc) throws FileNotFoundException, IOException,
			ParseException {
		Elements eleArr = doc.select(".pl-video.yt-uix-tile");
		List<Map<String, String>> lectureLst = new ArrayList<>();
		for (Element ele : eleArr) {
			try {
				Map<String, String> map = new HashMap<>();
				Element hele = ele.select(".pl-video-title-link").get(0);
				String link = hele.attr("href");
				String name = hele.text();

				map.put("name", name);
				map.put("link", link);

				lectureLst.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(ele);
				System.exit(0);
			}
		}
		return lectureLst;
	}
}
