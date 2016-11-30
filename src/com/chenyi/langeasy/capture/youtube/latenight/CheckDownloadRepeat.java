package com.chenyi.langeasy.capture.youtube.latenight;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chenyi.langeasy.capture.podcast.yalecourses.MatcherUtil;

public class CheckDownloadRepeat {
	static String dirPath = "E:/langeasy/lucene/youtube/latenight/";

	public static void main(String[] args) throws IOException {
		check();
	}

	public static void check() throws IOException {
		Set<String> vidLst = new HashSet<>();
		int total = 0;
		int ncTotal = 0;
		int ntTotal = 0;

		File sFile = new File(dirPath + "playlists.json");
		String sResult = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		JSONArray collectionList = new JSONArray(sResult);
		for (int i = 0; i < collectionList.length(); i++) {
			JSONObject collection = collectionList.getJSONObject(i);
			if (collection.has("videoLst")) {
				JSONArray videoLst = collection.getJSONArray("videoLst");
				for (int j = 0; j < videoLst.length(); j++) {
					JSONObject video = videoLst.getJSONObject(j);
					String link = video.getString("link");
					String vid = MatcherUtil.getVid(link);
					if (video.has("nocaption")) {
						ncTotal += 1;
					}

					vidLst.add(vid);
					total += 1;
				}
			}
		}
		File cdir = new File(dirPath + "caption/");
		for (File cfile : cdir.listFiles()) {
			long length = cfile.length();// in bytes
			if (length < 500) {// less than 0.5kb
				String filename = cfile.getName();
				System.out.println(filename + "\t" + length);
				ntTotal += 1;
			}
		}

		System.out.println(total);
		System.out.println("ncTotal " + ncTotal);
		System.out.println("ntTotal " + ntTotal);
		System.out.println(vidLst.size());
	}
}
