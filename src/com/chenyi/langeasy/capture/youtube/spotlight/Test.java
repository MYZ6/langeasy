package com.chenyi.langeasy.capture.youtube.spotlight;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chenyi.langeasy.capture.podcast.yalecourses.MatcherUtil;
import com.chenyi.langeasy.capture.youtube.ParseUtil;

public class Test {
	private static String dirPath = "E:/langeasy/lucene/youtube/spotlight/";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		String cpath = dirPath + "playlists.json";
		// cpath = dirPath + "playlists41/playlists.json";
		ParseUtil.count(cpath);

		int clipCount = 10;
		if (clipCount > 0) {
			// return;
		}

		int total = 0;
		int dlTotal = 0;
		int ncTotal = 0;
		int ntTotal = 0;
		for (int ci = 0; ci < clipCount; ci++) {
			String subDirPath = dirPath + "playlists" + (ci + 1) + "/";
			File file = new File(subDirPath + "playlists.json");

			String sResult = IOUtils.toString(new FileInputStream(file), "utf-8");

			JSONArray collectionList = new JSONArray(sResult);
			for (int i = 0; i < collectionList.length(); i++) {
				JSONObject collection = collectionList.getJSONObject(i);
				if (collection.has("videoLst")) {
					JSONArray videoLst = collection.getJSONArray("videoLst");
					total += videoLst.length();
					for (int j = 0; j < videoLst.length(); j++) {
						JSONObject video = videoLst.getJSONObject(j);
						String link = video.getString("link");
						String vid = MatcherUtil.getVid(link);

						String cfilePath = subDirPath + "caption/" + vid + ".xml";
						File cfile = new File(cfilePath);
						if (cfile.exists()) {
							dlTotal += 1;
							// long length = cfile.length();// in bytes
							// if (length < 500) {// less than 0.5kb
							// String filename = cfile.getName();
							// System.out.println(filename + "\t" + length);
							// ntTotal += 1;
							// video.put("notext-eng", 1);
							// }
							if (video.has("notext-eng")) {
								cfile.delete();
								// ntTotal += 1;
							}
						} else if (video.has("nocaption")) {
							ncTotal += 1;
						} else if (video.has("notext-eng")) {
							ntTotal += 1;
						}
					}
					// FileUtils.writeStringToFile(file, collectionList.toString(3), StandardCharsets.UTF_8);
				}
			}
		}
		System.out.println("video total: " + total);
		System.out.println("downloaded total: " + dlTotal);
		System.out.println("no caption total: " + ncTotal);
		System.out.println("no text caption total: " + ntTotal);
	}
}
