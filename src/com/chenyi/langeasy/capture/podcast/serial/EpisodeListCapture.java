package com.chenyi.langeasy.capture.podcast.serial;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EpisodeListCapture {
	private static String dirPath = "E:/langeasy/lucene/podcast/serial/";

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		System.out.println("start time is : " + new Date());
		archive1();
		// archive2();
		File sFile = new File(dirPath + "episode-list1.json");
		FileUtils.writeStringToFile(sFile, new JSONArray(episodeLst).toString(3), StandardCharsets.UTF_8);
		System.out.println("end time is : " + new Date());

	}

	static List<Map<String, String>> episodeLst = new ArrayList<>();

	public static void archive2() throws FileNotFoundException, IOException, ParseException {
		String url = "https://serialpodcast.org/season-two/listening-guide";
		File htmlFile = new File(dirPath + "archive2.html");
		String sResult = IOUtils.toString(new FileInputStream(htmlFile), "utf-8");
		Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Elements liArr = doc.select("h5 .episode-title");

		for (Element li : liArr) {
			String link = li.attr("href");
			String title = li.text();

			Map<String, String> map = new HashMap<>();

			map.put("link", link);
			map.put("title", title);

			episodeLst.add(map);
		}
	}

	public static void archive1() throws FileNotFoundException, IOException, ParseException {
		File htmlFile = new File(dirPath + "archive1.html");
		String sResult = IOUtils.toString(new FileInputStream(htmlFile), "utf-8");
		Document doc = Jsoup.parse(sResult);

		if (doc == null) {
			return;
		}
		Elements liArr = doc.select("a.song_name");

		for (Element li : liArr) {
			String link = li.attr("href");
			String title = li.text();

			Map<String, String> map = new HashMap<>();

			map.put("link", link);
			map.put("title", title);

			episodeLst.add(map);
		}
	}
}
