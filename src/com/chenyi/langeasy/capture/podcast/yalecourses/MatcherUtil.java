package com.chenyi.langeasy.capture.podcast.yalecourses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class MatcherUtil {
	private static String dirPath = "E:/langeasy/lucene/podcast/yale-courses/";

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File sFile = new File(dirPath + "video.html");
		String content = IOUtils.toString(new FileInputStream(sFile), "utf-8");

		// String url =
		// "https://www.youtube.com/watch?v=FGvWvsJcIEw&index=1&list=PLh9mgdi4rNeysmUGbzcgg-_zrFXNY3WrB";
		// System.out.println(url);
		// Document doc = CaptureUtil.timeoutRequest(url);
		// String content = doc.html();

		String ttsUrl = getTurl(content);
		System.out.println(ttsUrl);
	}

	public static String getTurl(String content) {
		String pattern = "'TTS_URL':\\s\"(.*)\",";

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = r.matcher(content);
		String ttsUrl = null;
		if (m.find()) {
			// System.out.println("Found value: " + m.group(0));
			ttsUrl = m.group(1);
			if ("".equals(ttsUrl)) {
				return null;
			}
			ttsUrl = StringEscapeUtils.unescapeJava(ttsUrl);
			ttsUrl += "&lang=en&fmt=srv3";
			ttsUrl += "&kind=asr";// sometimes no need this param
		}
		return ttsUrl;
	}

	public static String getTurl2(String ttsUrl) {
		ttsUrl = ttsUrl.substring(0, ttsUrl.length() - 9);
		return ttsUrl;
	}

	public static String getVid(String link) {
		String[] arr1 = link.split("&");
		// System.out.println(new JSONArray(arr1).toString(3));
		String pattern = "v=(.*)";

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		String vid = null;
		for (String param : arr1) {
			// Now create matcher object.
			Matcher m = r.matcher(param);
			if (m.find()) {
				// System.out.println("Found value: " + m.group(0));
				vid = m.group(1);
				// System.out.println(vid);
				break;
			}
		}
		return vid;
	}
}
