package com.chenyi.langeasy.capture.youtube.spotlight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class SplitPlaylistsFile {
	private static String dirPath = "E:/langeasy/lucene/youtube/spotlight/";

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

		File sFile = new File(dirPath + "playlists.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");
		JSONArray collectionList = new JSONArray(sResult);

		ArrayList<JSONObject> downloadLst = new ArrayList<>();
		for (int i = 1; i < collectionList.length(); i++) {
			JSONObject playlist = collectionList.getJSONObject(i);
			downloadLst.add(playlist);
		}
		int clipCount = 9;
		int plTotal = downloadLst.size();
		int step = plTotal / clipCount;
		clipCount += 1;
		step = 220;
		System.out.println(step + "\t" + clipCount + "\t" + plTotal);
		if (plTotal > -1) {
			// return;
		}
		for (int i = 0; i < clipCount; i++) {
			int start = i * step;
			int end = start + step;
			if (end > plTotal) {
				end = plTotal;
			}
			System.out.println(start + "\t" + end);

			List<JSONObject> subLst = downloadLst.subList(start, end);

			File subFile = new File(dirPath + "playlists" + (i + 1) + "/playlists.json");
			FileUtils.writeStringToFile(subFile, new JSONArray(subLst).toString(3), StandardCharsets.UTF_8);
		}
	}
}
