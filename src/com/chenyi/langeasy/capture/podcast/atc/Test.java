package com.chenyi.langeasy.capture.podcast.atc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test {

	private static String dirPath = "E:/langeasy/lucene/podcast/allthingsconsidered/";
	private static int yearCount;
	private static int startYear;
	static Map<Integer, JSONArray> episodeYearMap = new HashMap<>();
	private static ArrayList<JSONObject> downloadLst = new ArrayList<>();

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		System.out.println("start time is : " + new Date());
		startYear = 2005;
		yearCount = 11;

		int count = 0;
		for (int i = yearCount; i >= 0; i--) {
			int year = startYear + i;

			File sFile = new File(dirPath + "episode-list" + year + ".json");
			String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

			JSONArray episodeList = new JSONArray(sResult);

			for (int j = 0; j < episodeList.length(); j++) {
				JSONObject episode = episodeList.getJSONObject(j);
				if (episode.has("storyLst")) {
					JSONArray storyLst = episode.getJSONArray("storyLst");
					for (int k = 0; k < storyLst.length(); k++) {
						JSONObject story = storyLst.getJSONObject(k);
						String sid = story.getString("sid");
						File saveFile = new File(dirPath + File.separator + "transcript" + File.separator + sid
								+ ".html");
						if (!saveFile.exists()) {
							count++;
						}
					}
				}
			}
		}

		int total = downloadLst.size();
		System.out.println(count);

	}

}
