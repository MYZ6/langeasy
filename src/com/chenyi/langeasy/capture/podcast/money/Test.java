package com.chenyi.langeasy.capture.podcast.money;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test {
	static List<JSONObject> downloadLst = new ArrayList<>();

	private static String dirPath = "E:/langeasy/lucene/podcast/money/";

	public static void main(String[] args) throws Exception {
		System.out.println("start time is : " + new Date());

		File sFile = new File(dirPath + "episode-list2.json");
		// sFile = new File(dirPath + "test.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		// String sResult = new String(result, "utf-8");
		// sResult = StringEscapeUtils.unescapeJava(sResult);
		// System.out.println(sResult);
		JSONArray episodeList = new JSONArray(sResult);
		Date minDate = new Date();
		String minId = null;
		for (int i = 0; i < episodeList.length(); i++) {
			JSONObject episode = episodeList.getJSONObject(i);
			String edate = episode.getString("edate");
			Date date = DateUtils.parseDate(edate, new String[] { "yyyy-MM-dd" });
			if (date.compareTo(minDate) < 0) {
				minDate = date;
				minId = episode.getString("eid");
			}
		}
		System.out.println(minDate);
		System.out.println(minId);

	}
}
