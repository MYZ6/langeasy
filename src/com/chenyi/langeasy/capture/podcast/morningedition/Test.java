package com.chenyi.langeasy.capture.podcast.morningedition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test {
	private static String dirPath = "E:/langeasy/lucene/podcast/morningedition/";
	private static JSONArray episodeList;

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		System.out.println("start time is : " + new Date());

		File sFile = new File(dirPath + "episode-list.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		episodeList = new JSONArray(sResult);

		// for (int i = 0; i < episodeList.length(); i++) {
		// JSONObject episode = episodeList.getJSONObject(i);
		// if (episode.has("storyLst")) {
		// // episode.remove("storyLst");
		// }
		// }
		// FileUtils.writeStringToFile(sFile, episodeList.toString(3),
		// StandardCharsets.UTF_8);

		int count = 0;
		for (int i = 0; i < episodeList.length(); i++) {
			JSONObject episode = episodeList.getJSONObject(i);
			JSONArray storyLst = episode.getJSONArray("storyLst");
			for (int j = 0; j < storyLst.length(); j++) {
				JSONObject story = storyLst.getJSONObject(j);
				String sid = story.getString("sid");
				File saveFile = new File(dirPath + File.separator + "transcript" + File.separator + sid + ".html");
				if (!saveFile.exists()) {
					count++;
				}
			}
		}
		System.out.println(count);
	}
}
