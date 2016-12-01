package com.chenyi.langeasy.capture.youtube.oxford;

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
	public static void main(String[] args) throws IOException {
		String dirPath = "E:/langeasy/lucene/youtube/oxford/";
		check(dirPath);
	}

	public static void checkRepeat(String dirPath) throws IOException {
		Set<String> vidLst = new HashSet<>();
		int ctotal = 0;
		int total = 0;
		int zeroPtotal = 0;

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

					vidLst.add(vid);
					total += 1;
				}
				String count = collection.getString("count");
				count = count.replaceAll(",", "");
				int icount = Integer.parseInt(count);
				ctotal += icount;
				if (videoLst.length() != icount) {
					if (videoLst.length() == 0) {
						zeroPtotal += 1;
					}
					System.out.println("count: " + collection.getString("count") + "\tvideoLst length: "
							+ videoLst.length() + "\t" + collection.getString("name") + "\t"
							+ collection.getString("link"));
				}
			} else {
				System.out.println(collection);
			}
		}
		System.out.println(dirPath);

		System.out.println("playlist count video total: " + ctotal);
		System.out.println("video total: " + total);
		System.err.println("failed playlist count " + zeroPtotal);
		System.out.println("possible private video count " + (ctotal - total));
		System.out.println("no repeat video total: " + vidLst.size());
	}

	public static void check(String dirPath) throws IOException {
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
		System.out.println(dirPath);

		System.out.println("video total: " + total);
		System.out.println("no repeat video total: " + vidLst.size());
		System.out.println("no caption total " + ncTotal);
		System.out.println("no text or very little text Total " + ntTotal);
		int sdTotal = (vidLst.size() - ncTotal);
		System.out.println("supposed downloaed count " + sdTotal);
		int dTotal = cdir.list().length;
		System.out.println("caption files downloaded count: " + dTotal);
		int fTotal = sdTotal - dTotal;
		System.err.println("failed downloading count: " + fTotal);
	}
}
