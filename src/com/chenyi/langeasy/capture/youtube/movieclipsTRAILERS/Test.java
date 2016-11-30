package com.chenyi.langeasy.capture.youtube.movieclipsTRAILERS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Test {
	private static String dirPath = "E:/langeasy/lucene/youtube/movieclipsTRAILERS/";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String cpath = dirPath + "playlists.json";
		// ParseUtil.count(cpath);

		File sFile = new File(cpath);
		String content = IOUtils.toString(new FileInputStream(sFile), "utf-8");
		JSONArray courseArr = new JSONArray(content);
		int total = 0;
		int max = 0;
		for (int i = 0; i < courseArr.length(); i++) {
			JSONObject course = courseArr.getJSONObject(i);
			String count = course.getString("count");
			count = count.replaceAll(",", "");
			int icount = Integer.parseInt(count);
			total += icount;
			if (icount > max) {
				max = icount;
			}
			if (icount > 1000) {
				System.out.println(icount + "\t" + course.getString("name") + "\t" + course.getString("link"));
			}

		}
		System.out.println(total);
		System.out.println(max);
		if (total > -1) {
			// return;
		}
	}
}
