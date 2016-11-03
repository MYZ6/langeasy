package com.chenyi.langeasy.capture.podcast.tedtalks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test {
	private static String dirPath = "E:/langeasy/lucene/podcast/ted-talks/";

	public static void main(String[] args) throws FileNotFoundException, IOException {

		File sFile = new File(dirPath + "episode-list.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		JSONArray episodeList = new JSONArray(sResult);

		int count = 0;
		for (int j = 0; j < episodeList.length(); j++) {
			JSONObject episode = episodeList.getJSONObject(j);
			String link = episode.getString("link");
			String filename = link.substring(7, link.lastIndexOf("?language=en"));
			File saveFile = new File(dirPath + File.separator + "transcript" + File.separator + filename + ".html");
			if (!saveFile.exists()) {
				count++;
				System.out.println(filename);
			}
		}

		System.out.println(count);
	}
}
